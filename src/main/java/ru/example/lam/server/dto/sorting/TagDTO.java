package ru.example.lam.server.dto.sorting;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

/**
 * DTO для тегов
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TagDTO {

    @Parameter(description = "имя")
    private String name;

    @Parameter(description = "значение")
    private String value;
}
