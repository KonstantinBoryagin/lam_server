package ru.example.lam.server.dto.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import ru.example.lam.server.dto.journal.RecordDTO;
import ru.example.lam.server.dto.audit.enums.AuditMessageType;
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;

import static ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer.DATE_FORMAT;

/**
 * DTO для записи аудита в журнале
 *
 * @see RecordDTO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO для записи аудита")
public class AuditRecordDTO extends RecordDTO {

    @Schema(description = "id записи")
    private Long id;

    @JsonProperty("datetime")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @Schema(description = "время события", example = "10.12.2000 12:54:65.002", pattern = DATE_FORMAT)
    private LocalDateTime dateTime;

    @JsonProperty("messagetype")
    @Schema(name = "messagetype", description = "тип сообщения")
    private AuditMessageType auditMessageType;

    @JsonProperty("pageoffset")
    @Schema(description = "смещение страницы")
    private Long pageOffset;
}
