package ru.example.lam.server.dto.sorting;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO для параметров сортировки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "DTO для параметров фильтрации")
public class ItemDTO implements Serializable {

    @Schema(description = "тип")
    private String type;

    @Schema(description = "направление")
    private String direction;
}
