package ru.example.lam.server.dto.sup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import ru.example.lam.server.dto.sup.enums.SupParameterType;
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

import static ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer.DATE_FORMAT;

/**
 * DTO для записи в СУП
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для записи в СУП")
public class SupRecordDTO {

    @Schema(description = "id записи")
    private Long id;

    @JsonProperty("fullpath")
    @Schema(name = "fullpath", description = "глобальный путь (разделен через '.')")
    private String fullPath;

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

    @JsonProperty("createdatetime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(name = "createdatetime", description = "дата и время создания", example = "10.12.2000 12:54:65.002", pattern = DATE_FORMAT)
    private LocalDateTime createDatetime;

    @JsonProperty("updatetime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(name = "updatetime", description = "дата последнего обновления", example = "10.12.2000 12:54:65.002", pattern = DATE_FORMAT)
    private LocalDateTime updateTime;

    @Schema(description = "комментарий")
    private String comment;

    @JsonProperty("lastversion")
    @Schema(name = "lastversion", description = "последняя версия")
    private LastVersionDTO lastVersion;

    @JsonProperty("pageoffset")
    @Schema(description = "смещение страницы")
    private Long pageOffset;
}
