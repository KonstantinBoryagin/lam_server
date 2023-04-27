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
 * DTO для добавления записи в журнал
 *
 * @see RecordDTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "DTO для добавления записи Journal")
public class AddJournalDTO extends RecordDTO {

    @JsonProperty("messagetype")
    @Schema(description = "Тип сообщения")
    private JournalMessageType journalMessageType;

}
