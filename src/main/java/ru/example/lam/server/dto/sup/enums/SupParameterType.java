package ru.example.lam.server.dto.sup.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum хранит тип сообщения в СУП
 */
public enum SupParameterType {
    STRING("String"),
    JSON("Json"),
    ARRAY("Array");

    private final String value;

    SupParameterType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static SupParameterType fromString(String key) {
        return key == null
                ? null
                : SupParameterType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
