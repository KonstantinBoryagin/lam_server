package ru.example.lam.server.configuration.kafka;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.coreserver.exceptions.UserServiceException;
import ru.example.lam.server.entity.users.User;
import ru.example.lam.server.service.users.IUsersService;

import java.util.function.Consumer;

/**
 * Класс для формирования бинов, которые обрабатывают сообщения полученные из очереди
 */
@Configuration
@Log4j2
public class KafkaCustomChannels {

    private IUsersService userService;

    @Autowired
    public void setUserService(IUsersService userService) {
        this.userService = userService;
    }

    /**
     * Формирует потребителя для сохранения пользователя. Топик для чтения формируется из имени бина(+ настройки в свойствах)
     */
    @Bean
    public Consumer<UserDTO> saveUser() {
        return userDTO -> {
            log.debug("Received from the queue {}", userDTO);
            try {
                User createdUser = userService.addUser(userDTO);
                log.debug("User added {}", createdUser);
            } catch (UserServiceException ex) {
                log.error("User {} saved, but some roles were wrong: {}", ex.getUser(), ex.getWrongRoleValues());
            } catch (Exception ex) {
                log.error("Failed to save user {}", userDTO, ex);
            }
        };
    }
}
