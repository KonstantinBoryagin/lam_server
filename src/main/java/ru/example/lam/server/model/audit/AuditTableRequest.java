package ru.example.lam.server.model.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import ru.example.lam.server.dto.audit.enums.AuditMessageType;
import ru.example.lam.server.model.JournalPojo;

/**
 * POJO для транспортировки запроса таблицы аудита в журнале
 * @see JournalPojo
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditTableRequest extends JournalPojo {

    @Parameter(description = "смещение", example = "offset")
    private String offset;

    @Parameter(description = "лимит", example = "limit")
    private String limit;

    @JsonProperty("messagetype")
    @Parameter(name = "messagetype", description = "тип сообщения")
    private AuditMessageType auditMessageType;
}
