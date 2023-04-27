package ru.example.lam.server.repository.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.example.lam.server.entity.users.Role;
import ru.example.lam.server.entity.users.User;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    private static User expectedUserOne;
    static private List<Role> rolesList;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void init() {
        rolesList = new ArrayList<>(Arrays.asList(Role.builder().id(1L).roleValue("admin").build(),
                Role.builder().id(2L).roleValue("user").build(), Role.builder().id(3L).roleValue("manager").build()));
        expectedUserOne = User.builder()
                .id(1L)
                .username("Andy")
                .registrationTime(LocalDateTime.of(2004, 12, 31, 12, 54, 12))
                .city("Moscow")
                .active(true)
                .roles(new HashSet<>(rolesList))
                .build();
    }


    @Test
    @DisplayName("test for success findAll")
    void findAllTest() {
        List<User> usersList = userRepository.findAll();


        assertEquals(9, usersList.size());
        assertEquals(expectedUserOne, usersList.get(0));
    }

    @Test
    @DisplayName("test for wrong getAllUsers")
    void findAllUsersWrongTest() {
        List<User> usersList = userRepository.findAll();

        assertNotEquals(10, usersList.size());
    }

    @Test
    @DisplayName("test for success getUserById")
    void getUserByIdTest(){
        User actualUser = userRepository.findUserById(expectedUserOne.getId());

        assertEquals(expectedUserOne, actualUser);
    }

    @Test
    @DisplayName("test for wrong getUserById")
    void getUserByIdWrongTest() {
        Long id = 9999L;

        User actualUser = userRepository.findUserById(id);

        assertNull(actualUser);
    }

    @Test
    @DisplayName("test for success getUser by roleList")
    void getUsersByRolesTest() {
        List<String> roleList = new ArrayList<>(Arrays.asList("admin", "user"));
        Set<User> usersByRoles = userRepository.findUsersByRoles(roleList);
        assertNotNull(usersByRoles);
        assertEquals(5, usersByRoles.size());
    }

    @Test
    @DisplayName("test for success getUser by one role")
    void getUsersByRoleTest() {
        List<String> roleList = new ArrayList<>(Arrays.asList("admin"));
        Set<User> usersByRoles = userRepository.findUsersByRoles(roleList);
        assertNotNull(usersByRoles);
        assertEquals(2, usersByRoles.size());
    }

    @Test
    @DisplayName("test for success getUsersByRoles with wrong params")
    void getUsersByRolesEmptyTest() {
        List<String> roleList = new ArrayList<>(Arrays.asList("admin23", "manager43"));
        Set<User> usersByRoles = userRepository.findUsersByRoles(roleList);
        assertTrue(usersByRoles.isEmpty());
    }

    @Test
    @DisplayName("test for success addUser")
    void addUserTest() {
        User addUser = User.builder()
                .username("Andrew")
                .registrationTime(LocalDateTime.of(2004, 12, 31, 12, 54, 12))
                .active(true)
                .city("Kazan")
                .roles(new HashSet<>(rolesList))
                .build();
        User save = userRepository.save(addUser);
        addUser.setId(save.getId());

        assertEquals(addUser, save);
    }

    @Test
    @DisplayName("test for addUser with exception")
    void addWrongUserTest() {
        User addUser = User.builder()
                .registrationTime(LocalDateTime.of(2004, 12, 31, 12, 54, 12))
                .active(true)
                .city("Kazan")
                .roles(new HashSet<>(rolesList))
                .build();
        Throwable exception = assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(addUser));
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("test for success delete user by id")
    void deleteUserByIdTest() {
        Long userId = 1L;
        long counter = userRepository.deleteUserById(userId);
        assertEquals(1, counter);
    }

    @Test
    @DisplayName("test for success delete user by wrong id")
    void deleteUserByIdWrongTest() {
        Long userId = 100L;
        long counter = userRepository.deleteUserById(userId);
        assertEquals(0, counter);
    }
}