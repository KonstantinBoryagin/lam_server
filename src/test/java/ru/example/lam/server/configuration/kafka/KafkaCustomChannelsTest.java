package ru.example.lam.server.configuration.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.entity.users.User;
import ru.example.lam.server.service.kafka.MessagePublisher;
import ru.example.lam.server.service.users.implementation.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;

/**
 * Класс для тестирования получения сообщений из kafka по средствам SCS
 */
@BootTest
@Import(TestChannelBinderConfiguration.class)
class KafkaCustomChannelsTest {
    @Autowired
    private MessagePublisher messagePublisher;

    @MockBean
    private UserService usersService;
    private UserDTO userDTO;
    private User user;
    private static final Long USER_ID = 100L;

    @BeforeEach
    void init() {
        userDTO = UserDTO.builder()
                .username("Anna")
                .registrationTime(LocalDateTime.of(2000, 12, 30, 15, 45, 30))
                .city("Innopolis").build();
        user = User.builder()
                .id(USER_ID)
                .username(userDTO.getUsername())
                .registrationTime(userDTO.getRegistrationTime())
                .city(userDTO.getCity())
                .build();
    }

    @Test
    @DisplayName("test for success saveUser bean")
    void saveUserTest() throws Exception {
        Mockito.when(usersService.addUser(userDTO)).thenReturn(user);
        //отправляем DTO
        messagePublisher.publishSaveUserMessage(userDTO);
        //проверяем что сообщение было получено и вызван корректный метод с верными аргументами
        verify(usersService, times(1)).addUser(userDTO);
        verifyNoMoreInteractions(usersService);
    }

}