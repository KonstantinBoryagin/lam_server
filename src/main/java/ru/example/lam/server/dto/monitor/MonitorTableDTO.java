package ru.example.lam.server.dto.monitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import ru.example.lam.server.dto.monitor.enums.StatusType;

/**
 * DTO для таблицы в мониторинге событий
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для таблицы в мониторинге")
public class MonitorTableDTO {

    @Schema(description = "id таблицы")
    private Long id;

    @JsonProperty("servicename")
    @Schema(name = "servicename", description = "имя сервиса")
    private String serviceName;

    @JsonProperty("serviceaddress")
    @Schema(name = "serviceaddress", description = "адрес сервиса")
    private String serviceAddress;

    @JsonProperty("status")
    @Schema(name = "status", description = "статус")
    private StatusType statusType;

    @JsonProperty("updatetime")
    @Schema(name = "updatetime", description = "время последнего обновления статуса")
    private String updateTime;

    @Schema(description = "версия")
    private String version;
}
