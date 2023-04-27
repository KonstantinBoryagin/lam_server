package ru.example.lam.server.controller.users;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.configuration.annotation.MockMvcTest;
import ru.example.lam.server.controller.MockMvcTestUtil;
import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.entity.users.Role;
import ru.example.lam.server.entity.users.User;
import ru.example.lam.server.service.kafka.MessagePublisher;
import ru.example.lam.server.service.users.IUsersService;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_TITLE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_NOT_FOUND_CODE;
import static ru.example.lam.server.service.users.implementation.UserService.EMPTY_REQUEST_BODY_MESSAGE;
import static ru.example.lam.server.service.users.implementation.UserService.NOT_FOUND_RECORD_MESSAGE;

@MockMvcTest
@BootTest
class UserControllerTest {
    private final static String PARENT_URI = "http://localhost:8080/user";
    private final static String ALL_USERS_URI = "/all";
    private final static String USER_ID_URI = "/{userid}";
    private final static String ADD_USER_URI = "/adduser";
    private final static String ROLE_URI = "/roles";
    private static final int LIST_SIZE = 1;

    @Value("${AppSysName}")
    private String systemName;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsersService userService;

    @MockBean
    private MessagePublisher messagePublisher;

    private static User userOne;
    private static User userTwo;
    private static User userThree;
    private static UserDTO userDTO;

    @BeforeAll
    static void init() {
        testUsersInit();
    }

    @Test
    @DisplayName("test for get all users without params")
    void getAllUsersTest() throws Exception {
        String uri = PARENT_URI + ALL_USERS_URI;
        List<User> users = new ArrayList<>(Arrays.asList(userOne, userTwo, userThree));

        Mockito.when(userService.findAllWithPredicate(new UserDTO(), null, null)).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(get(uri))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpectAll(jsonPath("$.body", hasSize(users.size())))
                .andReturn();
        //проверяем что вернулись правильные объекты
        List<User> actualUsersList = convertResponseToUserRecord(mvcResult);
        assertEquals(users, actualUsersList);
    }

    @Test
    @DisplayName("test for get all users with params")
    void getAllUsersWithParamsTest() throws Exception {
        String uri = PARENT_URI + ALL_USERS_URI;
        List<User> users = new ArrayList<>(Arrays.asList(userTwo, userOne));
        UserDTO userDTO = UserDTO.builder().city("Moscow").build();
        List<String> sortList = new ArrayList<>(Arrays.asList("username"));
        String direction = "desc";

        Mockito.when(userService.findAllWithPredicate(userDTO, sortList, direction)).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(get(uri)
                        .param("city", "Moscow")
                        .param("sort", "username")
                        .param("direction", "desc"))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpectAll(jsonPath("$.body", hasSize(users.size())))
                .andReturn();
        //проверяем что вернулись правильные объекты
        List<User> actualUsersList = convertResponseToUserRecord(mvcResult);
        assertEquals(users, actualUsersList);
    }

    @Test
    @DisplayName("test for success getUserById")
    void getUserByIdTest() throws Exception {
        String uri = PARENT_URI + USER_ID_URI;
        Long userId = userOne.getId();

        Mockito.when(userService.getUserById(userId)).thenReturn(userOne);

        MvcResult mvcResult = mockMvc.perform(get(uri, userId))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(LIST_SIZE)))
                .andReturn();
        //проверяем что вернулся правильный объект
        List<User> userList = convertResponseToUserRecord(mvcResult);
        assertEquals(userOne, userList.get(0));
    }

    @Test
    @DisplayName("test for getUserById with exception")
    void getUserByIdWithExceptionTest() throws Exception {
        String uri = PARENT_URI + USER_ID_URI;
        Long userId = 1565L;

        Mockito.when(userService.getUserById(userId))
                .thenThrow(new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, userId)));

        mockMvc.perform(get(uri, userId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, userId)));
    }

    @Test
    @DisplayName("test for success getUsersByRoles")
    void getUsersByRolesTest() throws Exception {
        String uri = PARENT_URI + ROLE_URI;
        List<String> roleList = new ArrayList<>(Arrays.asList("admin", "user"));
        Set<User> userSet = new HashSet<>(Arrays.asList(userOne, userThree));
        Mockito.when(userService.getUsersByRoles(roleList)).thenReturn(userSet);

        MvcResult mvcResult = mockMvc.perform(get(uri)
                        .param("roles", "admin", "user")
                ).andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(userSet.size())))
                .andReturn();
        //проверяем что вернулся правильный объект
        List<User> userList = convertResponseToUserRecord(mvcResult);
        Set<User> actualSet = new HashSet<>(userList);
        assertEquals(userSet, actualSet);
    }

    @Test
    @DisplayName("test for get users by wrong roles")
    void getUsersByWrongRolesTest() throws Exception {
        String uri = PARENT_URI + ROLE_URI;
        List<String> roleList = new ArrayList<>(Arrays.asList("admin234", "user432"));
        Set<User> userSet = new HashSet<>();
        Mockito.when(userService.getUsersByRoles(roleList)).thenReturn(userSet);

        MvcResult mvcResult = mockMvc.perform(get(uri)
                        .param("roles", "admin234", "user432")
                ).andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(userSet.size())))
                .andReturn();
        //проверяем что вернулся правильный объект
        List<User> userList = convertResponseToUserRecord(mvcResult);
        Set<User> actualSet = new HashSet<>(userList);
        assertEquals(userSet, actualSet);
    }

    @Test
    @DisplayName("test for success addUser")
    void addUserTest() throws Exception {
        String uri = PARENT_URI + ADD_USER_URI;
        Mockito.when(messagePublisher.publishSaveUserMessage(userDTO)).thenReturn(true);
        Mockito.when(userService.addUser(userDTO)).thenReturn(userThree);

        mockMvc.perform(MockMvcTestUtil.postWithJson(uri, userDTO))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("test for wrong addUser")
    void addUserWrongTest() throws Exception {
        String uri = PARENT_URI + ADD_USER_URI;
        Mockito.when(messagePublisher.publishSaveUserMessage(userDTO)).thenReturn(false);

        mockMvc.perform(MockMvcTestUtil.postWithJson(uri, userDTO))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("test for success editUser")
    void editUserTest() throws Exception {
        Long userId = userThree.getId();
        String uri = PARENT_URI + "/" + userId;
        Mockito.when(userService.editUserById(userId, userDTO)).thenReturn(userThree);

        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.putWithJson(uri, userDTO))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(LIST_SIZE)))
                .andReturn();
        //проверяем что вернулся правильный объект
        List<User> userList = convertResponseToUserRecord(mvcResult);
        assertEquals(userThree, userList.get(0));
    }

    @Test
    @DisplayName("test for editUser with wrong id")
    void editUserWithWrongIdTest() throws Exception {
        long userId = -156L;
        String uri = PARENT_URI + "/" + userId;

        Mockito.when(userService.editUserById(userId, userDTO))
                .thenThrow(new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, userId)));

        mockMvc.perform(MockMvcTestUtil.putWithJson(uri, userDTO))
                .andExpectAll(MockMvcTestUtil.getResultWithError(HttpStatus.NOT_FOUND.value(), systemName,
                        ERROR_TITLE, String.format(NOT_FOUND_RECORD_MESSAGE, userId)));
    }

    @Test
    @DisplayName("test for editUser with empty body")
    void editUserWithEmptyBodyTest() throws Exception {
        long userId = 156L;
        String uri = PARENT_URI + "/" + userId;

        Mockito.when(userService.editUserById(userId, null))
                .thenThrow(new LamServerException(EMPTY_REQUEST_BODY_MESSAGE));

        mockMvc.perform(MockMvcTestUtil.putWithJson(uri, null))
                .andExpectAll(MockMvcTestUtil.getResultWithError(HttpStatus.NOT_FOUND.value(), systemName,
                        ERROR_TITLE, EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for success deleteUserById")
    void deleteUserByIdTest() throws Exception {
        long userId = 3L;
        String uri = PARENT_URI + USER_ID_URI;
        Mockito.when(userService.deleteUserById(userId)).thenReturn(true);

        mockMvc.perform(delete(uri, userId))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse());
    }

    @Test
    @DisplayName("test for wrong deleteUserById")
    void deleteUserByWrongIdTest() throws Exception {
        long userId = -3L;
        String uri = PARENT_URI + USER_ID_URI;
        Mockito.when(userService.deleteUserById(userId)).thenReturn(false);

        mockMvc.perform(delete(uri, userId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(false)),
                        (jsonPath("$.code").value(HttpStatus.NOT_FOUND.value())));
    }

    private List<User> convertResponseToUserRecord(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        return objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<User>>() {
        });
    }

    private static void testUsersInit() {
        Role admin = Role.builder().id(1L).roleValue("admin").build();
        Role user = Role.builder().id(2L).roleValue("user").build();
        Role manager = Role.builder().id(3L).roleValue("manager").build();
        userOne = User.builder()
                .id(1L)
                .username("Bob")
                .registrationTime(LocalDateTime.of(2010, 5, 3, 14, 30, 45))
                .active(true)
                .city("Moscow")
                .roles(new HashSet<>(Arrays.asList(admin, user)))
                .build();
        userTwo = User.builder()
                .id(333L)
                .username("John")
                .registrationTime(LocalDateTime.of(2000, 12, 31, 21, 45, 1))
                .active(false)
                .city("Moscow")
                .roles(new HashSet<>(Collections.singletonList(user)))
                .build();
        userThree = User.builder()
                .id(56L)
                .username("Alice")
                .registrationTime(LocalDateTime.of(2022, 1, 15, 22, 55, 6))
                .active(true)
                .city("Kazan")
                .roles(new HashSet<>(Arrays.asList(user, manager)))
                .build();
        userDTO = UserDTO.builder()
                .username("Alice")
                .active(true)
                .city("Kazan")
                .registrationTime(LocalDateTime.of(2022, 1, 15, 22, 55, 6))
                .rolesSet(new HashSet<>(Arrays.asList("user", "manager")))
                .build();
    }
}