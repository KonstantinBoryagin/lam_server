package ru.example.lam.server.dto.sup;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.util.List;

import ru.example.lam.server.dto.sup.enums.SupParameterType;

/**
 * DTO для новой записи СУП
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для новой записи СУП")
public class CreateSupDTO {

    @Schema(description = "параметр")
    private String parameter;

    @JsonProperty("parametername")
    @Schema(name = "parametername", description = "имя параметра")
    private String parameterName;

    @JsonProperty("subsystem")
    @Schema(name = "subsystem", description = "подсистема")
    private String subSystem;

    @JsonProperty("parametertype")
    @Schema(name = "parametertype", description = "тип параметра")
    private SupParameterType supParameterType;

    @Schema(description = "версия")
    private String version;

    @Schema(description = "имя пользователя")
    private String username;

    @JsonProperty("parametervalue")
    @ArraySchema(schema = @Schema(name = "parametervalue",
            description = "значение параметра. Формат: в зависимости от типа данных может быть представлен в виде " +
                    "однострочного и многострочного поля ввода, а также в виде списка с возможностью добавления и " +
                    "удаления элементов.",
            type = "string"))
    private List<String> parameterValueList;

    @Schema(description = "комментарий")
    private String comment;
}
