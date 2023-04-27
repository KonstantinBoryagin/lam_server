package ru.example.lam.server.dto.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import ru.example.lam.server.dto.audit.enums.AuditMessageType;
import ru.example.lam.server.dto.journal.RecordDTO;

/**
 * DTO для добавления записи аудита в журнал
 *
 * @see RecordDTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "DTO для добавления записи аудита")
public class AddAuditDTO extends RecordDTO {

    @JsonProperty("messagetype")
    @Schema(name = "messagetype", description = "тип сообщения")
    private AuditMessageType auditMessageType;


}
