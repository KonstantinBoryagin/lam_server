package ru.example.lam.server.coreserver.utils.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Кастомный десериализатор Jackson для DateFormatTime полей
 */
public class CustomDateDeserializer extends StdDeserializer<LocalDateTime> {
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss[.SSS]";
    private final transient DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern(DATE_FORMAT)
            .toFormatter();

    public CustomDateDeserializer() {
        this(null);
    }
    public CustomDateDeserializer(Class<LocalDateTime> t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String date = jsonparser.getText();
        return LocalDateTime.from(formatter.parse(date));
    }
}
