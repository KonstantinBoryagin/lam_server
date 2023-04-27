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
import ru.example.lam.server.model.enums.Position;
import ru.example.lam.server.dto.journal.enums.JournalMessageType;

/**
 * POJO для транспортировки запроса записи в журнале
 * @see JournalPojo
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalRecordRequestWithId extends JournalPojo {

    @Parameter(name = "position", description = "позиция записи, если не задана то - 'current'")
    private Position positionValue;

    @JsonProperty("messagetype")
    @Parameter(name = "messagetype", description = "тип сообщения")
    private JournalMessageType journalMessageType;

    @JsonProperty("pageoffset")
    @Parameter(name = "pageoffset", description = "смещение страницы")
    private Long pageOffset;

    @JsonProperty("elementsonpage")
    @Parameter(name = "elementsonpage", description = "количество записей на странице")
    private Integer elementsOnPage;
}
