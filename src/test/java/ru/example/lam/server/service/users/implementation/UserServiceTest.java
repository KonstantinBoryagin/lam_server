package ru.example.lam.server.service.users.implementation;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.exceptions.UserServiceException;
import ru.example.lam.server.entity.users.Role;
import ru.example.lam.server.entity.users.User;
import ru.example.lam.server.repository.users.RoleRepository;
import ru.example.lam.server.repository.users.UserRepository;
import ru.example.lam.server.service.users.IUsersService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;

@BootTest
class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private IUsersService usersService;

    private static UserDTO userDTO;

    @BeforeAll
    static void init() {
        userDTO = UserDTO.builder()
                .username("test")
                .city("test")
                .registrationTime(LocalDateTime.now())
                .rolesSet(new HashSet<>(Collections.singletonList("admin")))
                .build();
    }

    @Test
    @DisplayName("test for find all users without parameters")
    void findAllTest() {
        usersService.findAllWithPredicate(new UserDTO(), null, null);
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for find all users with two parameters")
    void findAllWithParametersTest() {
        UserDTO userDTO = UserDTO.builder().city("Moscow").build();
        List<String> sortList = new ArrayList<>(Collections.singletonList("username"));

        usersService.findAllWithPredicate(userDTO, sortList, null);
        verify(userRepository, times(1)).findAll(any(Predicate.class), any(Sort.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for find all users with all parameters")
    void findAllWithPredicateTest() {
        UserDTO userDTO = UserDTO.builder().city("Moscow").build();
        List<String> sortList = new ArrayList<>(Collections.singletonList("username"));
        String direction = "desc";

        usersService.findAllWithPredicate(userDTO, sortList, direction);
        verify(userRepository, times(1)).findAll(any(Predicate.class), any(Sort.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for get users by Roles")
    void getUsersByRolesTest() {
        List<String> roleList = new ArrayList<>(Arrays.asList("admin", "manager", "user"));

        usersService.getUsersByRoles(roleList);

        verify(userRepository, times(1)).findUsersByRoles(roleList);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for get user by id")
    void getUserByIdTest() throws LamServerException {
        Long id = 155L;
        when(userRepository.findUserById(id)).thenReturn(new User());

        usersService.getUserById(id);
        verify(userRepository, times(1)).findUserById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for add user")
    void addUserTest() throws UserServiceException, LamServerException {
        when(roleRepository.findAll()).thenReturn(new ArrayList<>(Collections.singletonList(Role.builder().id(1L).roleValue("admin").build())));
        usersService.addUser(userDTO);

        verify(roleRepository, times(1)).findAll();
        verifyNoMoreInteractions(roleRepository);
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for delete user by id")
    void deleteUserByIdTest() {
        Long id = 133L;

        usersService.deleteUserById(id);

        verify(userRepository, times(1)).deleteUserById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("test for edit user by id")
    void editUserByIdTest() throws UserServiceException, LamServerException {
        Long id = 133L;
        when(roleRepository.findAll()).thenReturn(new ArrayList<>(Collections.singletonList(Role.builder().id(1L).roleValue("admin").build())));
        when(userRepository.findUserById(id)).thenReturn(new User());

        usersService.editUserById(id, userDTO);

        verify(roleRepository, times(1)).findAll();
        verifyNoMoreInteractions(roleRepository);
        verify(userRepository, times(1)).findUserById(id);
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }
}