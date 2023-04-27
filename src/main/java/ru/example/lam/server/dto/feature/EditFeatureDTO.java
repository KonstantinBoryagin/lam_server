package ru.example.lam.server.dto.feature;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * DTO для изменения записи событий Feature
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "DTO для изменения записи событий Feature")
public class EditFeatureDTO extends CreateFeatureDTO {

    @Schema(description = "id")
    private Long id;
}
