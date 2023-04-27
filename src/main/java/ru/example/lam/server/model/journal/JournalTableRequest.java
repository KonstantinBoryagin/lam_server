package ru.example.lam.server.model.journal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import ru.example.lam.server.model.JournalPojo;
import ru.example.lam.server.dto.journal.enums.JournalMessageType;

/**
 * POJO для транспортировки запроса таблицы в журнале
 * @see JournalPojo
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalTableRequest extends JournalPojo{

    @Parameter(name = "offset", description = "смещение", example = "offset")
    private String offset;

    @Parameter(name = "limit", description = "лимит", example = "limit")
    private String limit;

    @JsonProperty("messagetype")
    @Parameter(name = "messagetype", description = "тип сообщения")
    private JournalMessageType journalMessageType;

}
