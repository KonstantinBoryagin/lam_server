package ru.example.lam.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.List;

import ru.example.lam.server.dto.sorting.ItemDTO;
import ru.example.lam.server.dto.sorting.TagDTO;

/**
 * POJO для транспортировки запроса записи/таблицы в журнале
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public class JournalPojo {

    @Parameter(name = "tags", hidden = true)
    private List<TagDTO> tagsDtoList;

    @Parameter(name = "sortby", hidden = true)
    private List<ItemDTO> itemsDtoList;

    @JsonProperty("datefrom")
    @Parameter(name = "datefrom", description = "дата начала", example = "30.12.2021")
    private String dateFrom;

    @JsonProperty("timefrom")
    @Parameter(name = "timefrom", description = "время начала", example = "15:30:45.123")
    private String timeFrom;

    @JsonProperty("dateto")
    @Parameter(name = "dateto", description = "дата окончания", example = "03.01.2022")
    private String dateTo;

    @JsonProperty("timeto")
    @Parameter(name = "timeto", description = "время окончания", example = "5:55:55.555")
    private String timeTo;

    @JsonProperty("subsystemname")
    @Parameter(name = "subsystemname", description = "подсистема", example = "subsystem name")
    private String subSystemName;

    @Parameter(name = "host", description = "DNS имя или ip адрес хоста", example = "127.0.0.1")
    private String host;

    @Parameter(name = "username", description = "имя пользователя", example = "user")
    private String username;

    @JsonProperty("userid")
    @Parameter(name = "userid", description = "id пользователя", example = "10001")
    private String userId;

    @Parameter(name = "message", description = "сообщение", example = "message")
    private String message;

    @JsonProperty("datasource")
    @Parameter(name = "datasource", description = "источник", example = "data source")
    private String dataSource;

    @Parameter(name = "method", description = "имя метода или url которое сгенерировало сообщение", example = "method")
    private String method;

    @Parameter(name = "namespace", description = "модуль в котором произошло событие", example = "namespace")
    private String namespace;

    @Parameter(name = "version", description = "версия сервиса", example = "1.0.0")
    private String version;
}
