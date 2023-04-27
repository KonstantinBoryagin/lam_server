package ru.example.lam.server.dto.audit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum хранит тип сообщения в аудите
 */
public enum AuditMessageType {
    CREATE("Create"),
    DELETE("Delete"),
    UPDATE("Update"),
    EXTRACT("Extract");

    private final String value;

    AuditMessageType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AuditMessageType fromString(String value) {
        return value == null
                ? null
                : AuditMessageType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
