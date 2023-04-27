package ru.example.lam.server.model.feature;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;
import ru.example.lam.server.dto.sorting.ItemDTO;

/**
 * POJO для параметров запроса событий Feature
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FeatureRequest {

    @JsonProperty("featurename")
    @Parameter(name = "featurename", description = "имя", example = "feature name")
    private String featureName;

    @JsonProperty("subsystemname")
    @Parameter(name = "subsystemname", description = "подсистема", example = "sub system name")
    private String subsystemName;

    @Parameter(description = "описание", example = "text")
    private String description;

    @JsonProperty("updatetime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = CustomDateDeserializer.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Parameter(name = "updatetime", description = "время последнего обновления", example = "10.12.2000 12:54:65.002")
    private LocalDateTime updateTime;

    @Parameter(description = "версия", example = "1.0.5")
    private String version;

    @JsonProperty("featurestatus")
    @Parameter(name = "featurestatus", description = "статус", schema = @Schema(type = "boolean"))
    private boolean featureStatus;

    @JsonProperty("pageoffset")
    @Parameter(name = "pageoffset", description = "смещение страницы")
    private Long pageOffset;

    @JsonProperty("elementsonpage")
    @Parameter(name = "elementsonpage", description = "количество записей на странице")
    private Integer elementsOnPage;

    @Parameter(description = "смещение", example = "offset")
    private String offset;

    @Parameter(description = "лимит", example = "limit")
    private String limit;

    @Parameter(hidden = true)
    private List<ItemDTO> itemList;

}
