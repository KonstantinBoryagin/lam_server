package ru.example.lam.server.dto.journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import ru.example.lam.server.dto.journal.enums.JournalMessageType;

/**
 * DTO для таблицы журнала событий
 *
 * @see TableDTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "DTO для таблицы журнала")
public class JournalTableDTO extends TableDTO {

    @JsonProperty("messagetype")
    @Schema(description = "тип сообщения")
    private JournalMessageType journalMessageType;

}
