package ru.example.lam.server.dto.journal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;
import ru.example.lam.server.dto.sorting.TagDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для записи в журнале
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RecordDTO {

    /** название подсистемы */
    @JsonProperty("subsystemname")
    @Schema(description = "название подсистемы")
    private String subSystemName;

    /** хост */
    @Schema(description = "хост")
    private String host;

    /** пользователь */
    @Schema(description = "пользователь")
    private String username;

    /** id пользователя */
    @JsonProperty("userid")
    @Schema(description = "id пользователя")
    private String userId;

    /** заголовок */
    @Schema(description = "заголовок")
    private String header;

    /** список тегов */
    @JsonProperty("tags")
    @Schema(description = "список тегов")
    private List<TagDTO> tagsDtoList;

    /** сообщение */
    @Schema(description = "сообщение")
    private String message;

    /** источник */
    @JsonProperty("datasource")
    @Schema(description = "источник")
    private String dataSource;

    /** вызванный метод */
    @Schema(description = "вызванный метод")
    private String method;

    /** пространство имен */
    @Schema(description = "пространство имен")
    private String namespace;

    /** входные данные */
    @Schema(description = "входные данные")
    private String request;

    /** выходные данные */
    @Schema(description = "выходные данные")
    private String response;

    /** версия сервиса */
    @Schema(description = "версия сервиса")
    private String version;

    @JsonProperty("eventtime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = CustomDateDeserializer.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(description = "Время события")
    private LocalDateTime eventTime;
}
