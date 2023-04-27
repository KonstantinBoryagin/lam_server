package ru.example.lam.server.service.users.implementation;

import brave.ScopedSpan;
import brave.Tracer;

import com.querydsl.core.types.Predicate;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.exceptions.UserServiceException;
import ru.example.lam.server.coreserver.utils.qpredicates.QPredicates;
import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.repository.users.RoleRepository;
import ru.example.lam.server.repository.users.UserRepository;
import ru.example.lam.server.service.mapping.UserMapper;
import ru.innopolis.lam.server.entity.users.QUser;
import ru.example.lam.server.entity.users.Role;
import ru.example.lam.server.entity.users.User;
import ru.example.lam.server.service.users.IUsersService;

@Service
@Log4j2
public class UserService implements IUsersService {
    public static final String EMPTY_REQUEST_BODY_MESSAGE = "request body is empty";
    public static final String NOT_FOUND_RECORD_MESSAGE = "Record with id %d not found";
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserMapper userMapper;
    private Tracer tracer;

    @Autowired
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAllWithPredicate(UserDTO userDTO, List<String> sortList, String direction) {
        log.debug("Method findAllWithPredicate is started");
        QPredicates qPredicate = QPredicates.builder()
                .add(userDTO.getUsername(), QUser.user.username::equalsIgnoreCase)
                .add(userDTO.getCity(), QUser.user.city::equalsIgnoreCase)
                .add(userDTO.getActive(), QUser.user.active::eq);
        log.debug("QPredicates {}", qPredicate);

        if(userDTO.getRolesSet() != null) {
            for (String role : userDTO.getRolesSet()) {
                qPredicate.add(role, QUser.user.roles.any().roleValue::equalsIgnoreCase);
                log.debug("qPredicate added {}", qPredicate);

            }
        }
        Sort.Direction directionFromString;
        try {
            directionFromString = Sort.Direction.fromString(direction);
            log.debug("Direction from string {}", directionFromString);
        } catch (IllegalArgumentException ex) {
            directionFromString = Sort.Direction.ASC;
            log.error("Failed direction from string {}", directionFromString, ex);
        }

        Predicate predicate = qPredicate.buildAnd();
        Iterable<User> all = null;
        ScopedSpan newSpan = tracer.startScopedSpan("postgresql");
        try {
            if (sortList != null && predicate != null) {
                all = userRepository.findAll(predicate, Sort.by(directionFromString, sortList.toArray(new String[0])));
                log.debug("Users found {}", all);
            } else if (predicate != null) {
                all = userRepository.findAll(predicate);
                log.debug("Users found {}", all);
            } else if (sortList != null) {
                all = userRepository.findAll(Sort.by(directionFromString, sortList.toArray(new String[0])));
                log.debug("Users found {}", all);
            } else {
                all = userRepository.findAll();
                log.debug("Users found {}", all);
            }
        } finally {
            newSpan.tag("data.base", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }

        return StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Set<User> getUsersByRoles(List<String> roleList) {
        log.debug("Method getUsersByRoles {} is started", roleList);
        ScopedSpan newSpan = tracer.startScopedSpan("postgresql");
        try {
        return userRepository.findUsersByRoles(roleList);
        } finally {
            newSpan.tag("data.base", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
    }

    @Override
    public User getUserById(Long userId) throws LamServerException {
        log.debug("Method getUserById {} is started", userId);
        ScopedSpan newSpan = tracer.startScopedSpan("postgresql");
        try {
            User userById = userRepository.findUserById(userId);
            log.debug("User by id {} found", userId);
            if (userById != null) {
                return userById;
            } else {
                log.error(String.format(NOT_FOUND_RECORD_MESSAGE, userId));
                throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, userId));
            }
        } finally {
            newSpan.tag("data.base", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
    }

    @Override
    public User addUser(UserDTO userDTO) throws LamServerException, UserServiceException {
        log.debug("Method addUser is started");
        if (userDTO == null) {
            log.error("Failed to add user {}", EMPTY_REQUEST_BODY_MESSAGE);
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        }
        List<String> wrongRoleValues = new ArrayList<>();
        ScopedSpan newSpan = tracer.startScopedSpan("postgresql");
        try {
            List<Role> rolesList = roleRepository.findAll();
            log.debug("Roles list found {}", rolesList);
            User user = userMapper.fromDtoToUser(userDTO, rolesList, wrongRoleValues);
            log.debug("From dto to user {}", user);
            User saveUser = userRepository.save(user);
            log.debug("User saved {}", saveUser);
            if (!wrongRoleValues.isEmpty()) {
                log.error("Wrong role values is not empty {}", wrongRoleValues);
                throw new UserServiceException(saveUser, wrongRoleValues);
            } else {
                return saveUser;
            }
        } finally {
            newSpan.tag("data.base", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
    }

    @Override
    public boolean deleteUserById(Long id) {
        log.debug("Method deleteUserById is started");
        ScopedSpan newSpan = tracer.startScopedSpan("postgresql");
        try {
        return userRepository.deleteUserById(id) == 1;
        } finally {
            newSpan.tag("data.base", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
    }

    @Override
    public User editUserById(Long userId, UserDTO userDTO) throws LamServerException, UserServiceException {
        log.debug("Method editUserById is started");
        if (userDTO == null) {
            log.error("Failed to get user {}", EMPTY_REQUEST_BODY_MESSAGE);
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        }
        List<String> wrongRoleValues = new ArrayList<>();
        ScopedSpan newSpan = tracer.startScopedSpan("postgresql");
        try {
            User userById = userRepository.findUserById(userId);
            log.debug("User by id {} found", userId);
            if (userById == null) {
                log.error(String.format(NOT_FOUND_RECORD_MESSAGE, userId));
                throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, userId));
            }
            List<Role> rolesList = roleRepository.findAll();
            log.debug("Roles list found {}", rolesList);
            userById = userMapper.fromDtoToUser(userDTO, rolesList, wrongRoleValues);
            log.debug("From dto to user {}", userById);
            userById.setId(userId);
            User saveUser = userRepository.save(userById);
            log.debug("User saved {}", saveUser);
            if (!wrongRoleValues.isEmpty()) {
                log.error("Wrong role values is not empty {}", wrongRoleValues);
                throw new UserServiceException(saveUser, wrongRoleValues);
            } else {
                return saveUser;
            }
        } finally {
            newSpan.tag("data.base", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
    }
}
