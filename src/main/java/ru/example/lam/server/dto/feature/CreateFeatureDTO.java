package ru.example.lam.server.dto.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * DTO для создания записи событий Feature
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для создания записи событий Feature")
public class CreateFeatureDTO {

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

    @Schema(description = "версия")
    private String version;

    @Schema(description = "комментарий")
    private String comment;
}
