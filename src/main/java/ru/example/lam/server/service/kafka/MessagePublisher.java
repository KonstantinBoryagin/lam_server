package ru.example.lam.server.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import ru.example.lam.server.dto.users.UserDTO;

/**
 * Класс для отправки сообщений в очередь с помощью {@link StreamBridge}
 */
@Component
public class MessagePublisher {
    public static final String USER_OUT_TOPIC = "saveUser-out-0";
    private final StreamBridge streamBridge;

    @Autowired
    public MessagePublisher(StreamBridge streamBridge ){
        this.streamBridge = streamBridge;
    }

    public boolean publishSaveUserMessage(UserDTO userDTO){
        return streamBridge.send(USER_OUT_TOPIC, userDTO);
    }

}
