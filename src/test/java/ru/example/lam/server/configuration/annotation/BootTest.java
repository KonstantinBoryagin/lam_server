package ru.example.lam.server.configuration.annotation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация для тестовых классов (базовая конфигурация + настройка встроенной Kafka)
 */
@SpringBootTest(properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles("test")

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BootTest {
}
