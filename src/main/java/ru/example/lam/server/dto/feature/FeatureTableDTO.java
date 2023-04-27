package ru.example.lam.server.dto.feature;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

import static ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer.DATE_FORMAT;


/**
 * DTO для таблицы событий Feature
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для таблицы событий Feature")
public class FeatureTableDTO {

    @Schema(description = "id")
    private Long id;

    @JsonProperty("featurename")
    @Schema(name = "featurename", description = "имя")
    private String featureName;

    @JsonProperty("featurestatus")
    @Schema(name = "featurestatus", description = "статус", type = "boolean")
    private boolean featureStatus;

    @Schema(description = "подсистема")
    private String subsystem;

    @Schema(description = "описание")
    private String description;

    @JsonProperty("updatetime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(name = "updatetime", description = "время последнего обновления",
            example = "10.12.2000 12:54:65.002", pattern = DATE_FORMAT)
    private LocalDateTime updateTime;

    @Schema(description = "версия")
    private String version;
}
