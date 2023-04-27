package ru.example.lam.server.coreserver.transport;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * DTO для транспортного контракта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "DTO для транспортного контракта")
public class TransportDTO<T> {

    @Schema(description = "индикатор успешности операции")
    private Boolean success;

    @Schema(description = "код")
    private Integer code;

    @ArraySchema(schema = @Schema(description = "объекты ответа"))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> body;

    @ArraySchema(schema = @Schema(description = "объекты ошибок"))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ErrorDTO<Exception>> errors;

    @ArraySchema(schema = @Schema(description = "объекты сообщений"))
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<MessageDTO<String>> message;
}
