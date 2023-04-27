package ru.example.lam.server.controller.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;

import ru.example.lam.server.configuration.swagger.SwaggerConfig;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.aspect.Loggable;
import ru.example.lam.server.dto.monitor.AddMonitorDTO;
import ru.example.lam.server.dto.monitor.MonitorChartDTO;
import ru.example.lam.server.dto.monitor.MonitorRecordDTO;
import ru.example.lam.server.dto.monitor.MonitorTableDTO;
import ru.example.lam.server.model.monitor.MonitorRequest;
import ru.example.lam.server.service.mapping.TableRequestMapper;
import ru.example.lam.server.service.monitor.IMonitorService;
import ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl;

/**
 * RestController для обработки запросов мониторинга событий
 */
@RestController
@RequestMapping(value = "/api/monitor", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Monitoring", description = "Взаимодействие с мониторингом")
@Log4j2
public class MonitorController {
    private IMonitorService monitorService;
    private BaseTransportServiceImpl baseTransportService;
    private TableRequestMapper requestMapper;
    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setMonitorService(IMonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Autowired
    public void setBaseTransportService(BaseTransportServiceImpl baseTransportService) {
        this.baseTransportService = baseTransportService;
    }

    @Autowired
    public void setRequestMapper(TableRequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }

    /**
     * Получение таблицы мониторинга событий
     *
     * @param itemList                 List с параметрами и направлением сортировки
     * @param requestParam             входящие параметры get запроса
     * @param monitorRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/monitortable")
    @Operation(summary = "Получение таблицы мониторинга",
            description = "Получение таблицы мониторинга с входящими параметрами для фильтрации")
    public TransportDTO<MonitorTableDTO> getMonitorTable(
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(value = "sortby", required = false) List<String> itemList,
            @ParameterObject MonitorRequest monitorRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParam) {
        MonitorTableDTO monitorTableDTO = null;
        try {
            MonitorRequest monitorRequest = objectMapper.convertValue(requestParam, MonitorRequest.class);
            log.debug("Monitor request converted {}", monitorRequest);
            requestMapper.completeTheTableRequest(monitorRequest, itemList);
            log.debug("The table request completed {}", monitorRequest);
            monitorTableDTO = monitorService.getMonitorTable(monitorRequest);
            log.info("Method getMonitorTable is done");
            return baseTransportService.createSuccessfulTransportDTO(monitorTableDTO);
        } catch (Exception ex) {
            log.error("Failed to get monitor table", ex);
            return baseTransportService.createErrorTransportDTO(monitorTableDTO, ex);
        }
    }

    /**
     * Получение записи в мониторинге событий по id
     *
     * @param monitorRecordId id записи в мониторинге событий
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{monitorrecordid}")
    @Operation(summary = "Получение записи мониторинга по id", description = "Получение записи мониторинга по id")
    public TransportDTO<MonitorRecordDTO> getMonitorRecordById(
            @PathVariable(name = "monitorrecordid")
            @Parameter(required = true, example = "100", description = "id записи мониторинга") Long monitorRecordId) {

        MonitorRecordDTO monitorRecordDTO = null;
        try {
            monitorRecordDTO = monitorService.getMonitorRecordById(monitorRecordId);
            log.info("Method getMonitorRecordById {} is done", monitorRecordId);
            return baseTransportService.createSuccessfulTransportDTO(monitorRecordDTO);
        } catch (LamServerException ex) {
            log.error("Failed to get monitor record by id {}", monitorRecordId, ex);
            return baseTransportService.createErrorTransportDTO(monitorRecordDTO, ex);
        }
    }

    /**
     * Удаление записи в мониторинге событий по id
     *
     * @param monitorRecordId id записи в мониторинге событий
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{monitorrecordid}")
    @Operation(summary = "Удаление записи мониторинга по id", description = "Удаление записи мониторинга по id")
    public TransportDTO<MonitorRecordDTO> deleteMonitorRecordById(
            @PathVariable(name = "monitorrecordid")
            @Parameter(required = true, example = "100", description = "id записи мониторинга") Long monitorRecordId) {

        MonitorRecordDTO monitorRecordDTO = null;
        try {
            boolean result = monitorService.deleteMonitorRecordById(monitorRecordId);
            log.debug("Method delete monitor record by id {} is done", monitorRecordId);
            if (result) {
                log.info("Method deleteMonitorRecordById {} is {}", monitorRecordId, result);
                return baseTransportService.createEmptySuccessfulTransportDTO(monitorRecordDTO);
            } else {
                log.info("Method deleteMonitorRecordById {} is {}", monitorRecordId, result);
                return baseTransportService.createEmptyErrorTransportDTO(monitorRecordDTO, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception ex) {
            log.error("Failed to delete monitor record by id {}", monitorRecordId, ex);
            return baseTransportService.createErrorTransportDTO(monitorRecordDTO, ex);
        }
    }

    /**
     * Получение записи в мониторинге событий по имени и версии сервиса
     *
     * @param serviceName имя сервиса в мониторинге событий
     * @param version     версия сервиса в мониторинге событий
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{servicename}/{version}")
    @Operation(summary = "Получение записи мониторинга по имени и версии сервиса",
            description = "Получение записи мониторинга по имени и версии сервиса")
    public TransportDTO<MonitorRecordDTO> getMonitorRecordByServiceNameAndVersion(
            @PathVariable(name = "servicename")
            @Parameter(description = "имя сервиса", example = "serviceName") String serviceName,
            @PathVariable(name = "version")
            @Parameter(description = "версия сервиса", example = "1.0.0") String version) {

        MonitorRecordDTO monitorRecordDTO = null;
        try {
            monitorRecordDTO = monitorService.getMonitorRecordByServiceNameAndVersion(serviceName, version);
            log.info("Method get monitor record by service name {} and version {}", serviceName, version);
            return baseTransportService.createSuccessfulTransportDTO(monitorRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get monitor record by service name {} and version {}", serviceName, version, ex);
            return baseTransportService.createErrorTransportDTO(monitorRecordDTO, ex);
        }
    }

    /**
     * Добавление записи в мониторинг событий
     *
     * @param addMonitorDTO запись для добавления в мониторинг событий
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/addmonitor")
    @Operation(summary = "Добавление записи в мониторинг", description = "Добавление записи в мониторинг")
    public TransportDTO<MonitorTableDTO> addMonitorRecord(@RequestBody(required = false) AddMonitorDTO addMonitorDTO) {

        MonitorTableDTO monitorTableDTO = null;
        try {
            monitorTableDTO = monitorService.addMonitorRecord(addMonitorDTO);
            log.info("Method addMonitorRecord {} is done", addMonitorDTO);
            return baseTransportService.createSuccessfulTransportDTO(monitorTableDTO);
        } catch (Exception ex) {
            log.error("Failed to add monitor record {}", addMonitorDTO, ex);
            return baseTransportService.createErrorTransportDTO(monitorTableDTO, ex);
        }
    }

    /**
     * Добавление записи в мониторинг событий по id
     *
     * @param monitorRecordId id записи в мониторинге событий
     * @param monitorChartDTO запись мониторинга которая будет добавлена
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/monitorchart/{monitorrecordid}")
    @Operation(summary = "Добавление записи в мониторинг по id", description = "Добавление записи в мониторинг по id")
    public TransportDTO<MonitorRecordDTO> putRecordToMonitorChart(
            @PathVariable(name = "monitorrecordid")
            @Parameter(example = "100", description = "id записи мониторинга") Long monitorRecordId,
            @RequestBody(required = false) MonitorChartDTO monitorChartDTO) {

        MonitorRecordDTO monitorRecordDTO = null;
        try {
            monitorRecordDTO = monitorService.putRecordToMonitorChart(monitorRecordId, monitorChartDTO);
            log.info("Method put record to monitor chart {} by id {} is done", monitorChartDTO, monitorRecordId);
            return baseTransportService.createSuccessfulTransportDTO(monitorRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to put record to monitor chart {} by id {}",  monitorChartDTO, monitorRecordId, ex);
            return baseTransportService.createErrorTransportDTO(monitorRecordDTO, ex);
        }
    }

    /**
     * Скрыть/показать запись в мониторинге событий
     *
     * @param monitorRecordId id записи в мониторинге событий
     * @param hide            параметр для определения состояния записи (скрыть или показывать)
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/hidemonitor/{monitorrecordid}")
    @Operation(summary = "Скрыть/показать запись в мониторинге событий",
            description = "Скрыть/показать запись в мониторинге событий")
    public TransportDTO<MonitorRecordDTO> hideOrShowMonitorRecord(
            @PathVariable(name = "monitorrecordid")
            @Parameter(required = true, example = "100", description = "id записи мониторинга") Long monitorRecordId,
            @RequestParam(value = "hide", required = false, defaultValue = "true")
            @Parameter(description = "показать/скрыть запись, по умолчанию 'true'",
                    schema = @Schema(type = "boolean", defaultValue = "true")) boolean hide) {

        MonitorRecordDTO monitorRecordDTO = null;
        try {
            monitorRecordDTO = monitorService.hideOrShowMonitorRecord(monitorRecordId, hide);
            log.info("Method hideOrShowMonitorRecord by id {} is done", monitorRecordId);
            return baseTransportService.createSuccessfulTransportDTO(monitorRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to hide or show monitor record by id {}", monitorRecordId, ex);
            return baseTransportService.createErrorTransportDTO(monitorRecordDTO, ex);
        }
    }

    /**
     * Получение записи в мониторинге событий по id и фильтру
     *
     * @param monitorRecordId          id записи в мониторинге событий
     * @param itemsList                List с параметрами и направлением сортировки
     * @param requestParam             входящие параметры get запроса
     * @param monitorRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/monitortable/{monitorrecordid}")
    @Operation(summary = "Получение записи мониторинга из таблицы по id",
            description = "Получение записи мониторинга из таблицы по id с параметрами для фильтрации")
    public TransportDTO<MonitorRecordDTO> getMonitorRecordFromTable(
            @PathVariable(name = "monitorrecordid")
            @Parameter(required = true, example = "100", description = "id записи мониторинга") Long monitorRecordId,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(value = "sortby", required = false) List<String> itemsList,
            @ParameterObject MonitorRequest monitorRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParam) {

        MonitorRequest monitorRequest = objectMapper.convertValue(requestParam, MonitorRequest.class);
        log.debug("Monitor request converted {}", monitorRequest);
        MonitorRecordDTO monitorRecordDTO = null;
        try {
            requestMapper.completeTheRecordRequestWithId(monitorRequest, itemsList);
            log.debug("The record request with id {} completed {}", monitorRecordId, monitorRequest);
            monitorRecordDTO = monitorService.getMonitorRecordFromTable(monitorRecordId, monitorRequest);
            log.info("Method getMonitorRecordFromTable by id {} is done", monitorRecordId);
            return baseTransportService.createSuccessfulTransportDTO(monitorRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get monitor record from table by id {}", monitorRecordId, ex);
            return baseTransportService.createErrorTransportDTO(monitorRecordDTO, ex);
        }
    }
}
