package ru.example.lam.server.dto.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * DTO для записи в мониторинге событий
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO для записи в мониторинге")
public class MonitorRecordDTO extends MonitorTableDTO implements Serializable {

    @Schema(description = "показать/скрыть запись")
    private boolean hide;

    @JsonProperty("cpuload")
    @Schema(name = "cpuload", description = "загрузка ЦПУ")
    private ChartDTO cpuLoad;

    @JsonProperty("memoryusage")
    @Schema(name = "memoryusage", description = "потребление памяти")
    private ChartDTO memoryUsage;

    @JsonProperty("networkload")
    @Schema(name = "networkload", description = "нагрузка на сеть")
    private ChartDTO networkLoad;

    @JsonProperty("worktime")
    @Schema(name = "worktime", description = "время работы")
    private ChartDTO workTime;

}
