package ru.example.lam.server.dto.monitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO для добавления записи в мониторинг событий
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для записи мониторинга")
public class AddMonitorDTO implements Serializable {

    @JsonProperty("servicename")
    @Schema(name = "servicename", description = "имя сервиса")
    private String serviceName;

    @JsonProperty("serviceaddress")
    @Schema(name = "serviceaddress", description = "адрес сервиса")
    private String serviceAddress;

    @Schema(description = "версия")
    private String version;
}
