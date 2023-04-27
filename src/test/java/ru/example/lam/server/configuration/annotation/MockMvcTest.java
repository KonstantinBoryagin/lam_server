package ru.example.lam.server.configuration.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для тестов REST контроллеров с использованием {@link MockMvc}
 */
@SpringBootTest(properties = {"spring.liquibase.enabled=false", "keycloak.enabled=false"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application-test.properties")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockMvcTest {
}
