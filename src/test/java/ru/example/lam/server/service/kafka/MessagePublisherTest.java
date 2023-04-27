package ru.example.lam.server.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import ru.example.lam.server.dto.users.UserDTO;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для тестирования отправки сообщения в kafka по средствам SCS.
 * Свойство "spring.cloud.function.definition=none" необходимо для того что бы встроенный получатель не перехватил сообщение.
 */
@SpringBootTest(properties = {"spring.liquibase.enabled=false", "spring.cloud.function.definition=none"})
@ActiveProfiles("test")
@Import(TestChannelBinderConfiguration.class)
class MessagePublisherTest {

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessagePublisher messagePublisher;

    private UserDTO userDTO;

    @BeforeEach
    void init() {
        userDTO = UserDTO.builder()
                .username("Anna")
                .registrationTime(LocalDateTime.of(2000, 12, 30, 15, 45, 30))
                .city("Innopolis").build();
    }

    @Test
    @DisplayName("test for success publishSaveUserMessage method")
    void publishSaveUserMessageTest() throws IOException {
        //отправляем сообщение
        messagePublisher.publishSaveUserMessage(userDTO);
        //получаем
        Message<byte[]> saveUser = outputDestination.receive(100, "saveUser");
        UserDTO receiveUserDTO = objectMapper.readValue(saveUser.getPayload(), UserDTO.class);
        //сравниваем
        assertEquals(userDTO, receiveUserDTO);
    }
}