package ru.example.lam.server.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.exceptions.UserServiceException;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.aspect.Loggable;
import ru.example.lam.server.entity.users.User;
import ru.example.lam.server.service.kafka.MessagePublisher;
import ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl;
import ru.example.lam.server.service.users.IUsersService;

import static ru.example.lam.server.service.kafka.MessagePublisher.USER_OUT_TOPIC;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
public class UserController {
    private IUsersService userService;
    private BaseTransportServiceImpl baseTransportService;
    private ObjectMapper objectMapper;

    private MessagePublisher messagePublisher;

    @Autowired
    public void setMessagePublisher(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setBaseTransportService(BaseTransportServiceImpl baseTransportService) {
        this.baseTransportService = baseTransportService;
    }

    @Autowired
    public void setUserService(IUsersService userService) {
        this.userService = userService;
    }

    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/all")
    public TransportDTO<User> getAllUsers(@RequestParam(required = false) Map<String, String> requestParams,
                                          @RequestParam(value = "rolelist", required = false) List<String> roleList,
                                          @RequestParam(value = "sort", required = false) List<String> sortList,
                                          @RequestParam(value = "direction", required = false) String direction) {
        try {
            UserDTO userRequest = objectMapper.convertValue(requestParams, UserDTO.class);
            log.debug("User request converted {}", userRequest);
            if (roleList != null) {
                userRequest.setRolesSet(new HashSet<>(roleList));
                log.debug("User request set roles set {}", userRequest);
            }
            List<User> allWithPredicate = userService.findAllWithPredicate(userRequest, sortList, direction);
            log.info("Method getAllUser is done");
            return baseTransportService.createSuccessfulTransportDTO(allWithPredicate.toArray(new User[0]));
        } catch (Exception ex) {
            log.error("Failed to get all user", ex);
            return baseTransportService.createErrorTransportDTO(new User(), ex);
        }
    }

    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{userid}")
    public TransportDTO<User> getUserByID(@PathVariable(value = "userid", required = false) Long userId) {
        User user = null;
        try {
            user = userService.getUserById(userId);
            log.info("Method getUserByID {} is done", userId);
            return baseTransportService.createSuccessfulTransportDTO(user);
        } catch (LamServerException ex) {
            log.error("Failed to get user by id {}", userId, ex);
            return baseTransportService.createErrorTransportDTO(user, ex);
        }
    }

    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/roles")
    public TransportDTO<User> getUsersByRoles(@RequestParam(value = "roles", required = false) List<String> roleList) {
        Set<User> usersByRole = userService.getUsersByRoles(roleList);
        log.info("Method getUsersByRoles is done");
        return baseTransportService.createSuccessfulTransportDTO(usersByRole.toArray(new User[0]));

    }

    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping(value = "/adduser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TransportDTO<User> addUser(@RequestBody(required = false) UserDTO userDTO) {
        boolean sendToQueue = messagePublisher.publishSaveUserMessage(userDTO);
        if(sendToQueue) {
            log.debug("Method addUser() {} is done", userDTO);
            return baseTransportService.createEmptySuccessfulTransportDTO(new User());
        } else {
            log.error("The object {} was not sent to the queue {}", userDTO, USER_OUT_TOPIC);
            return baseTransportService.createEmptyErrorTransportDTO(new User(), HttpStatus.NOT_FOUND.value());
        }
    }

    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{userid}")
    public TransportDTO<User> editUser(@PathVariable(value = "userid", required = false) Long userId,
                                       @RequestBody(required = false) UserDTO userDTO) {
        User user = null;
        try {
            user = userService.editUserById(userId, userDTO);
            log.debug("User {} edited by id {}", userDTO, userId);
            return baseTransportService.createSuccessfulTransportDTO(user);
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to edit user {} by id {}", userDTO, userId, ex);
            return baseTransportService.createErrorTransportDTOWithCause(user, ex);
        } catch (UserServiceException ex) {
            log.error("Failed to edit user {} by id {}", userDTO, userId, ex);
            return baseTransportService.createSuccessfulTransportDTOWithMessage(ex.getWrongRoleValues(), ex.getUser());
        } catch (Exception ex) {
            log.error("Failed to edit user {} by id {}", userDTO, userId, ex);
            return baseTransportService.createErrorTransportDTO(user, HttpStatus.NOT_FOUND.value(), ex);
        }
    }

    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{userid}")
    public TransportDTO<User> deleteUserById(@PathVariable(value = "userid") Long userId) {
        User user = new User();
        if (userService.deleteUserById(userId)) {
            log.debug("User deleted by id {}", userId);
            return baseTransportService.createEmptySuccessfulTransportDTO(user);
        } else {
            log.error("Failed to delete user by id {}", userId);
            return baseTransportService.createEmptyErrorTransportDTO(user, HttpStatus.NOT_FOUND.value());
        }
    }
}
