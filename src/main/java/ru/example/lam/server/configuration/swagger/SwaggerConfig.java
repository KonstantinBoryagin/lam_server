package ru.example.lam.server.configuration.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.example.lam.server.service.mapping.TableRequestMapper.PARAMETER_SEPARATOR;

/**
 * Класс для конфигурации Swagger документации
 */
@Configuration
public class SwaggerConfig {

    public static final String DESCRIPTION_FOR_TAGS_LIST =
            "список тегов разделенный '" + PARAMETER_SEPARATOR + "', может не содержать правой части выражения";

    public static final String DESCRIPTION_FOR_ITEMS_LIST =
            "список параметров сортировки разделенный '" + PARAMETER_SEPARATOR + "', обе части выражения обязательны";

    public static final String TAGS_EXAMPLE = "name=value";
    public static final String ITEMS_EXAMPLE = "type=direction";

    @Value("${AppSysName}")
    private String systemName;
    @Value("${project.version}")
    private String projectVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title(String.format("%s swagger documentation", systemName))
                .version(projectVersion)
                .contact(
                        new Contact()
                                .email("konstantin.boryagin@innopolis.ru")
                                .url("https://gitlab.sezinno.ru/boryagin")
                                .name("Konstantin Boryagin")
                )
        );
    }
}
