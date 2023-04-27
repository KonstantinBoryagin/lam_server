package ru.example.lam.server.coreserver.utils.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

import ru.example.lam.server.entity.journalandaudit.JournalMessageTypeEntity;
import ru.example.lam.server.dto.journal.enums.JournalMessageType;

/**
 * Конвертер для преобразования {@link JournalMessageType} в {@link JournalMessageTypeEntity}
 * и наоборот.
 */
@Converter(autoApply = true)
public class JournalMessageTypeConverter implements AttributeConverter<JournalMessageType, String> {

    @Override
    public String convertToDatabaseColumn(JournalMessageType category) {
        if (category == null) {
            return null;
        }
        return category.getValue();
    }

    @Override
    public JournalMessageType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(JournalMessageType.values())
                .filter(c -> c.getValue().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
