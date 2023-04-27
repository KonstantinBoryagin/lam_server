package ru.example.lam.server.coreserver.transport;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * DTO для ошибок {@link TransportDTO}
 *
 * @param <E> Exception или его наследники
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "DTO для ошибок")
public class ErrorDTO<E extends Exception> {

    @Schema(description = "код ошибки")
    private Integer code;

    @Schema(description = "система")
    private String system;

    @Schema(description = "заголовок")
    private String title;

    @Schema(description = "сообщение")
    private String message;

    @ArraySchema(schema = @Schema(description = "информация"))
    private List<E> body;
}
