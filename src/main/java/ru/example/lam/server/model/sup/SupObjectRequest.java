package ru.example.lam.server.model.sup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.util.List;

import ru.example.lam.server.dto.sorting.ItemDTO;
import ru.example.lam.server.dto.sup.enums.SupParameterType;
import ru.example.lam.server.model.enums.Position;

/**
 * POJO для транспортировки запроса таблицы записей СУП
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupObjectRequest {

    @Parameter(name = "position", description = "позиция")
    private Position positionValue;

    @Parameter(description = "параметр", example = "parameter")
    private String parameter;

    @JsonProperty("parametername")
    @Parameter(name = "parametername", description = "имя параметра", example = "parameter name")
    private String parameterName;

    @JsonProperty("subsystemname")
    @Parameter(name = "subsystemname", description = "подсистема", example = "sub system name")
    private String subSystemName;

    @JsonProperty("fullpath")
    @Parameter(name = "fullpath", description = "глобальный путь (разделен через '.')",
            example = "app.subsys1.paramgroup1.param")
    private String fullPath;

    @Parameter(description = "имя пользователя", example = "admin")
    private String username;

    @JsonProperty("parametertype")
    @Parameter(name = "parametertype", description = "тип параметра")
    private SupParameterType supParameterType;

    @Parameter(description = "смещение", example = "offset")
    private String offset;

    @Parameter(description = "лимит", example = "limit")
    private String limit;

    @Parameter(hidden = true)
    private List<ItemDTO> itemDTOList;

    @JsonProperty("pageoffset")
    @Parameter(name = "pageoffset", description = "смещение страницы")
    private Long pageOffset;

    @JsonProperty("elementsonpage")
    @Parameter(name = "elementsonpage", description = "количество записей на странице")
    private Integer elementsOnPage;

}
