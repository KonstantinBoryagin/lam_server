package ru.example.lam.server.dto.journal.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum хранит тип записи в журнале
 */
public enum JournalMessageType {
    DEBUG("Debug"),
    WARNING("Warning"),
    ERROR("Error"),
    FATAL("Fatal"),
    TRACE("Trace"),
    INFO("Info");
    private final String value;

    JournalMessageType(String value) {
        this.value = value;
    }

    /**
     * Метод для десериализации в enum
     *
     * @param value значение поля ENUM
     * @return {@link JournalMessageType}
     */
    @JsonCreator
    public static JournalMessageType fromString(String value) {
        return value == null
                ? null
                : JournalMessageType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
