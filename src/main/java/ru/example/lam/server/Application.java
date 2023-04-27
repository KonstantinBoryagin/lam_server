package ru.example.lam.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author Konatantin Boryagin
 * @version 0.0.1
 */
@SpringBootApplication
@RestController
public class Application {

    @Value("${default.timezone}")
    private String defaultTimezone;

    /**
     * Возвращает 200-й http статус на GET запрос consul о доступности сервиса
     * @return http status 200
     */
    @GetMapping("/health-check")
    public ResponseEntity<String> consulHealthCheck() {
        String message = "Service active";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostConstruct
    public void init() {
        // Устанавливаем для SpringBoot часовой пояс
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezone));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
