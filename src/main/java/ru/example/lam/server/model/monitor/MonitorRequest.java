package ru.example.lam.server.model.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;

import java.util.List;

import ru.example.lam.server.dto.sorting.ItemDTO;
import ru.example.lam.server.dto.monitor.enums.StatusType;

/**
 * POJO класс для параметров запроса мониторинга
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitorRequest {

    @Parameter(description = "версия", example = "1.0.5")
    private String version;

    @JsonProperty("servicename")
    @Parameter(name = "servicename", description = "имя сервиса", example = "service name")
    private String serviceName;

    @JsonProperty("serviceaddress")
    @Parameter(name = "serviceaddress", description = "адрес сервиса", example = "127.0.0.1")
    private String serviceAddress;

    @JsonProperty("status")
    @Parameter(name = "status", description = "статус")
    private StatusType statusType;

    @Parameter(description = "смещение", example = "offset")
    private String offset;

    @Parameter(description = "лимит", example = "limit")
    private String limit;

    @Parameter(hidden = true)
    private List<ItemDTO> itemsList;

    @JsonProperty("showall")
    @Parameter(name = "showall", description = "показать все", schema = @Schema(type = "boolean"))
    private boolean showAll;

    @JsonProperty("pageoffset")
    @Parameter(name = "pageoffset", description = "смещение страницы")
    private Long pageOffset;

    @JsonProperty("elementsonpage")
    @Parameter(name = "elementsonpage", description = "количество записей на странице")
    private Integer elementsOnPage;
}
