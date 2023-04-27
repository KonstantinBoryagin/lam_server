package ru.example.lam.server.dto.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import ru.example.lam.server.dto.journal.TableDTO;
import ru.example.lam.server.dto.audit.enums.AuditMessageType;

/**
 * DTO для таблицы аудита журнала
 *
 * @see TableDTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "DTO для таблицы аудита")
public class AuditTableDTO extends TableDTO {

    @JsonProperty("messagetype")
    @Schema(name = "messagetype", description = "тип сообщения")
    private AuditMessageType auditMessageType;

}
