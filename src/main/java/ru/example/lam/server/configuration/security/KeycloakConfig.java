package ru.example.lam.server.configuration.security;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Keycloak
 */
@Configuration
public class KeycloakConfig {

    /**
     * Определяем, что мы хотим использовать файл свойств Spring вместо keycloak.json(использующийся по умолчанию)
     */
    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

}
