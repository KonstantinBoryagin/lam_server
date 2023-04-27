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
 * DTO для графика в мониторинге событий
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для графика в мониторинге")
public class MonitorChartDTO implements Serializable {

    @Schema(description = "id")
    private Long id;

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
