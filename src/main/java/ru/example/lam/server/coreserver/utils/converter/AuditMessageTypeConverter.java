package ru.example.lam.server.coreserver.utils.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

import ru.example.lam.server.dto.audit.enums.AuditMessageType;
import ru.example.lam.server.entity.journalandaudit.AuditMessageTypeEntity;

/**
 * Конвертер для преобразования {@link AuditMessageType} в {@link AuditMessageTypeEntity}
 * и наоборот.
 */
@Converter(autoApply = true)
public class AuditMessageTypeConverter implements AttributeConverter<AuditMessageType, String> {

    @Override
    public String convertToDatabaseColumn(AuditMessageType category) {
        if (category == null) {
            return null;
        }
        return category.getValue();
    }

    @Override
    public AuditMessageType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(AuditMessageType.values())
                .filter(c -> c.getValue().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
