package ru.example.lam.server.controller.audit;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

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

import java.util.List;
import java.util.Map;

import ru.example.lam.server.configuration.swagger.SwaggerConfig;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.dto.audit.AddAuditDTO;
import ru.example.lam.server.dto.audit.AuditRecordDTO;
import ru.example.lam.server.dto.audit.AuditTableDTO;
import ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl;
import ru.example.lam.server.aspect.Loggable;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;
import ru.example.lam.server.service.audit.IAuditService;
import ru.example.lam.server.service.mapping.TableRequestMapper;

/**
 * RestController для обработки событий аудита
 */
@RestController
@RequestMapping(value = "/api/audit", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit", description = "Взаимодействие с аудитом")
@Log4j2
public class AuditController {
    private IAuditService auditService;
    private BaseTransportServiceImpl baseTransportService;
    private TableRequestMapper tableRequestMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setAuditService(IAuditService auditService) {
        this.auditService = auditService;
    }

    @Autowired
    public void setBaseTransportService(BaseTransportServiceImpl baseTransportService) {
        this.baseTransportService = baseTransportService;
    }

    @Autowired
    public void setTableRequestMapper(TableRequestMapper tableRequestMapper) {
        this.tableRequestMapper = tableRequestMapper;
    }

    /**
     * Получение таблицы аудита событий
     *
     * @param tagsList                    List тегов
     * @param itemsList                   List с параметрами и направлением сортировки
     * @param requestParams               входящие параметры get запроса
     * @param auditTableRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/audittable")
    @Operation(summary = "Получение таблицы аудита",
            description = "Получение таблицы аудита с входящими параметрами для фильтрации")
    public TransportDTO<AuditTableDTO> getAuditTable(
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_TAGS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.TAGS_EXAMPLE)))
            @RequestParam(required = false, value = "tags") List<String> tagsList,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(required = false, value = "sortby") List<String> itemsList,
            @ParameterObject AuditTableRequest auditTableRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParams) {
        AuditTableDTO auditTable = null;
        try {
            AuditTableRequest auditTableRequest = objectMapper.convertValue(requestParams, AuditTableRequest.class);
            log.debug("Audit table request converted {}", auditTableRequest);
            tableRequestMapper.completeTheTableRequest(auditTableRequest, tagsList, itemsList);
            log.debug("The table request completed {}", auditTableRequest);
            auditTable = auditService.getAuditTable(auditTableRequest);
            log.info("Method getAuditTable is done");
            return baseTransportService.createSuccessfulTransportDTO(auditTable);
        } catch (Exception ex) {
            log.error("Failed to get audit table", ex);
            return baseTransportService.createErrorTransportDTO(auditTable, ex);
        }
    }

    /**
     * Получение записи в журнале аудита по id
     *
     * @param auditRecordId id записи в журнале
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{auditrecordid}")
    @Operation(summary = "Получение записи аудита по id", description = "Получение записи аудита по id")
    public TransportDTO<AuditRecordDTO> getAuditRecordById(
            @PathVariable(name = "auditrecordid")
            @Parameter(required = true, example = "100", description = "id записи аудита") Long auditRecordId) {
        AuditRecordDTO auditRecordById = null;
        try {
            auditRecordById = auditService.getAuditRecordById(auditRecordId);
            log.info("Method getAuditRecordById {} is done", auditRecordId);
            return baseTransportService.createSuccessfulTransportDTO(auditRecordById);
        } catch (Exception e) {
            log.error("Failed to get audit record by id {}", auditRecordId, e);
            return baseTransportService.createErrorTransportDTO(auditRecordById, e);
        }
    }

    /**
     * Удаление записи из журнала аудита по id
     *
     * @param auditRecordId id записи для удаления
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{auditrecordid}")
    @Operation(summary = "Удаление записи аудита по id", description = "Удаление записи аудита по id")
    public TransportDTO<AuditRecordDTO> deleteAuditRecordById(
            @PathVariable(name = "auditrecordid")
            @Parameter(required = true, example = "100", description = "id записи аудита") Long auditRecordId) {

        AuditRecordDTO auditRecordDTO = null;
        try {
            boolean result = auditService.deleteAuditRecordById(auditRecordId);
            log.debug("Audit record by id {} deleted", auditRecordId);
            if (result) {
                log.info("Method deleteAuditRecordById {} is {}", auditRecordId, result);
                return baseTransportService.createEmptySuccessfulTransportDTO(auditRecordDTO);
            } else {
                log.info("Method deleteAuditRecordById {} is {}", auditRecordId, result);
                return baseTransportService.createEmptyErrorTransportDTO(auditRecordDTO, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception ex) {
            log.error("Failed to delete audit record by id {}", auditRecordId, ex);
            return baseTransportService.createErrorTransportDTO(auditRecordDTO, ex);
        }
    }

    /**
     * Добавление записи в журнал аудита
     *
     * @param addAuditDTO запись для добавления в журнал аудита
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/addaudit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Добавление записи аудита", description = "Добавление записи аудита")
    public TransportDTO<AuditRecordDTO> addAudit(@RequestBody(required = false) AddAuditDTO addAuditDTO) {
        AuditRecordDTO auditRecordDTO = null;
        try {
            auditRecordDTO = auditService.addAudit(addAuditDTO);
            log.info("Method addAudit {} is done", addAuditDTO);
            return baseTransportService.createSuccessfulTransportDTO(auditRecordDTO);
        } catch (Exception e) {
            log.error("Failed to add audit {}", addAuditDTO, e);
            return baseTransportService.createErrorTransportDTO(auditRecordDTO, e);
        }
    }

    /**
     * Получение записи в журнале аудита по id, фильтру, относительной позиции
     *
     * @param auditRecordId                      id записи в журнале аудита
     * @param position                           относительная позиция записи в журнале аудита
     * @param tagsList                           List тегов
     * @param itemsList                          List с параметрами и направлением сортировки
     * @param requestParams                      входящие параметры get запроса
     * @param auditRecordRequestWithIdForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     * @throws LamServerException {@link LamServerException} исключение с описанием причины
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/audittable/{auditrecordid}")
    @Operation(summary = "Получение записи аудита из таблицы по id",
            description = "Получение записи аудита из таблицы по id с параметрами для фильтрации")
    public TransportDTO<AuditRecordDTO> getAuditRecordByIdWithFilters(
            @PathVariable(name = "auditrecordid")
            @Parameter(required = true, example = "100", description = "id записи аудита") Long auditRecordId,
            @RequestParam(required = false, value = "position") @Parameter(name = "position", hidden = true) String position,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_TAGS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.TAGS_EXAMPLE)))
            @RequestParam(required = false, value = "tags") List<String> tagsList,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(required = false, value = "sortby") List<String> itemsList,
            @ParameterObject AuditRecordRequestWithId auditRecordRequestWithIdForSwagger,
            @RequestParam(required = false) Map<String, String> requestParams) throws LamServerException {

        AuditRecordRequestWithId auditRecordRequestWithId = objectMapper.convertValue(requestParams, AuditRecordRequestWithId.class);
        log.debug("Audit record request by id {} with filters converted {}", auditRecordId, auditRecordRequestWithId);
        tableRequestMapper.completeTheRecordRequestWithId(auditRecordRequestWithId,
                tagsList, itemsList, position);
        log.debug("The record request by id {} with filters completed {}", auditRecordId, auditRecordRequestWithId);
        AuditRecordDTO auditRecordDTO = null;

        try {
            auditRecordDTO = auditService.getAuditRecordByIdWithFilters(auditRecordId, auditRecordRequestWithId);
            log.info("Method get audit record by id {} with filters is done", auditRecordId);
            return baseTransportService.createSuccessfulTransportDTO(auditRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get audit request by id {} with filters", auditRecordId, ex);
            return baseTransportService.createErrorTransportDTO(auditRecordDTO, ex);
        }
    }
}
