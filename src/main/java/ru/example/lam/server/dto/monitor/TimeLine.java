package ru.example.lam.server.dto.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

/**
 * DTO для значения координат графика {@link ChartDTO}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для значения координат графика")
public class TimeLine implements Serializable {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = CustomDateDeserializer.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(description = "время", example = "10.12.2000 12:54:65.002", pattern = CustomDateDeserializer.DATE_FORMAT)
    private LocalDateTime time;

    @Schema(description = "значение")
    private String value;
}
