package ru.example.lam.server.dto.monitor.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum хранит статус сервиса
 */
public enum StatusType {
    ONLINE("Online"),
    OFFLINE("Offline"),
    ERROR("Error"),
    DEBUG("Debug");

    private final String value;

    StatusType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static StatusType fromString(String value) {
        return value == null
                ? null
                : StatusType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
