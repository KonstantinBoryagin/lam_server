package ru.example.lam.server.dto.journal;

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
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

import java.time.LocalDateTime;

/**
 * DTO для таблицы записей журнала
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TableDTO {

    @Schema(description = "id")
    private Long id;

    @JsonProperty("datetime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = CustomDateDeserializer.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(description = "время события", example = "10.12.2000 12:54:65.002", pattern = CustomDateDeserializer.DATE_FORMAT)
    private LocalDateTime dateTime;

    @JsonProperty("subsystemname")
    @Schema(description = "подсистема")
    private String subSystemName;

    @Schema(description = "хост")
    private String host;

    @Schema(description = "имя пользователя")
    private String username;

    @Schema(description = "заголовок")
    private String header;
}
