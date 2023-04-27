package ru.example.lam.server.dto.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * DTO для графика
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO для графика")
public class ChartDTO implements Serializable {

    @Schema(description = "описание")
    private String description;

    @JsonProperty("currentvalue")
    @Schema(name = "currentvalue", description = "текущее значение")
    private String currentValue;

    @JsonProperty("timeline")
    @Schema(name = "timeline", description = "график")
    private TimeLine timeLine;

}
