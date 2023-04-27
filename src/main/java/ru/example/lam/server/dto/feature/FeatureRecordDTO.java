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
 * DTO для записи событий Feature
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "DTO для записи событий Feature")
public class FeatureRecordDTO extends FeatureTableDTO {

    @Schema(description = "имя пользователя")
    private String username;

    @Schema(description = "комментарий")
    private String comment;

    @JsonProperty("pageoffset")
    @Schema(description = "смещение страницы")
    private Long pageOffset;
}
