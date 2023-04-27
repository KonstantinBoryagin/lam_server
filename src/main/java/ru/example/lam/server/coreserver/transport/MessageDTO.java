package ru.example.lam.server.coreserver.transport;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для сообщений {@link TransportDTO}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "DTO для сообщений")
public class MessageDTO<S> {

    @Schema(description = "заголовок")
    private String title;

    @Schema(description = "сообщение")
    private String message;

    @ArraySchema(schema = @Schema(description = "тело сообщения"))
    private List<S> body;
}
