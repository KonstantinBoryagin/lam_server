package ru.example.lam.server.controller.sup;

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
import ru.example.lam.server.dto.sup.CreateSupDTO;
import ru.example.lam.server.dto.sup.EditSupDTO;
import ru.example.lam.server.dto.sup.SupRecordDTO;
import ru.example.lam.server.dto.sup.SupTableDTO;
import ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl;
import ru.example.lam.server.aspect.Loggable;
import ru.example.lam.server.model.sup.SupObjectRequest;
import ru.example.lam.server.service.mapping.TableRequestMapper;
import ru.example.lam.server.service.sup.ISupService;

/**
 * RestController для обработки событий СУП
 */
@RestController
@RequestMapping(value = "/api/sup", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "СУП", description = "Взаимодействие с СУП")
@Log4j2
public class SupController {
    private ISupService supService;
    private BaseTransportServiceImpl baseTransportService;
    private TableRequestMapper requestMapper;
    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setRequestMapper(TableRequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }

    @Autowired
    public void setSupService(ISupService supService) {
        this.supService = supService;
    }

    @Autowired
    public void setBaseTransportService(BaseTransportServiceImpl baseTransportService) {
        this.baseTransportService = baseTransportService;
    }


    /**
     * Получение таблицы СУП
     *
     * @param itemList                   List с параметрами и направлением сортировки
     * @param requestParams              входящие параметры get запроса
     * @param supObjectRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/suptable")
    @Operation(summary = "Получение таблицы СУП с параметрами для фильтрации",
            description = "Получение таблицы СУП с параметрами для фильтрации")
    public TransportDTO<SupTableDTO> getSupTable(
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(value = "sortby", required = false) List<String> itemList,
            @ParameterObject SupObjectRequest supObjectRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParams) {

        SupTableDTO supTable = null;
        try {
            SupObjectRequest supObjectRequest = objectMapper.convertValue(requestParams, SupObjectRequest.class);
            log.debug("Sup object request converted {}", supObjectRequest);
            requestMapper.completeTheTableRequest(supObjectRequest, itemList);
            log.debug("The table request completed {}", supObjectRequest);
            supTable = supService.getSupTable(supObjectRequest);
            log.info("Method getSupTable is done");
            return baseTransportService.createSuccessfulTransportDTO(supTable);
        } catch (Exception ex) {
            log.error("Failed to get sup table", ex);
            return baseTransportService.createErrorTransportDTO(supTable, ex);
        }
    }

    /**
     * Получение записи СУП по id
     *
     * @param supRecordId id записи СУП
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{suprecordid}")
    @Operation(summary = "Получение записи СУП по id", description = "Получение записи СУП по id")
    public TransportDTO<SupRecordDTO> getSupRecordByID(
            @PathVariable(name = "suprecordid")
            @Parameter(description = "id записи", example = "100") Long supRecordId) {

        SupRecordDTO supRecordDTO = null;
        try {
            supRecordDTO = supService.getSupRecordByID(supRecordId);
            log.info("Method getSupRecordByID {} is done", supRecordId);
            return baseTransportService.createSuccessfulTransportDTO(supRecordDTO);
        } catch (LamServerException ex) {
            log.error("Failed to get sup record by id {}", supRecordId, ex);
            return baseTransportService.createErrorTransportDTO(supRecordDTO, ex);
        }
    }

    /**
     * Удаление записи СУП по id
     *
     * @param supRecordId id записи СУП
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{suprecordid}")
    @Operation(summary = "Удаление записи СУП по id", description = "Удаление записи СУП по id")
    public TransportDTO<SupRecordDTO> deleteSupRecordById(
            @PathVariable(name = "suprecordid")
            @Parameter(description = "id записи", example = "100") Long supRecordId) {

        SupRecordDTO supRecordDTO = null;
        try {
            boolean result = supService.deleteSupRecordById(supRecordId);
            log.debug("Sup record by id {} deleted", supRecordId);
            if (result) {
                log.info("Method deleteSupRecordById {} is {}", supRecordId, result);
                return baseTransportService.createEmptySuccessfulTransportDTO(supRecordDTO);
            } else {
                log.info("Method deleteSupRecordById {} is {}", supRecordId, result);
                return baseTransportService.createEmptyErrorTransportDTO(supRecordDTO, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception ex) {
            log.error("Failed to delete sup record by id {}", supRecordId, ex);
            return baseTransportService.createErrorTransportDTO(supRecordDTO, ex);
        }
    }

    /**
     * Получение записи СУП по глобальному параметру пути
     *
     * @param fullPath глобальный параметр пути
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/fullpath/{fullpath}")
    @Operation(summary = "Получение записи СУП по глобальному параметру пути",
            description = "Получение записи СУП по глобальному параметру пути")
    public TransportDTO<SupRecordDTO> getSupRecordByFullPath(
            @PathVariable(value = "fullpath")
            @Parameter(description = "глобальный путь (разделен через '.')", example = "app.subsys1.paramgroup1.param") String fullPath) {

        SupRecordDTO supRecordDTO = null;
        try {
            supRecordDTO = supService.getSupRecordByFullPath(fullPath);
            log.info("Method getSupRecordByFullPath {} is done", fullPath);
            return baseTransportService.createSuccessfulTransportDTO(supRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get sup record by full path {}", fullPath, ex);
            return baseTransportService.createErrorTransportDTO(supRecordDTO, ex);
        }
    }

    /**
     * Создание записи СУП
     *
     * @param createSupDTO запись для добавления в СУП
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/createsup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Создание записи СУП", description = "Создание записи СУП")
    public TransportDTO<SupRecordDTO> createSupRecord(@RequestBody(required = false) CreateSupDTO createSupDTO) {

        SupRecordDTO supRecordDTO = null;
        try {
            supRecordDTO = supService.createSupRecord(createSupDTO);
            log.info("Method createSupRecord {} is done", createSupDTO);
            return baseTransportService.createSuccessfulTransportDTO(supRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to create sup record {}", createSupDTO, ex);
            return baseTransportService.createErrorTransportDTO(supRecordDTO, ex);
        }
    }

    /**
     * Изменение записи СУП
     *
     * @param editSupDTO параметры для изменения записи СУП
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/editsup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Изменение записи СУП", description = "Изменение записи СУП")
    public TransportDTO<SupRecordDTO> editSupRecord(@RequestBody(required = false) EditSupDTO editSupDTO) {

        SupRecordDTO supRecordDTO = null;
        try {
            supRecordDTO = supService.editSupRecord(editSupDTO);
            log.info("Method editSupRecord {} is done", editSupDTO);
            return baseTransportService.createSuccessfulTransportDTO(supRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to edit sup record {}", editSupDTO, ex);
            return baseTransportService.createErrorTransportDTO(supRecordDTO, ex);
        }
    }

    /**
     * Получение записи СУП по id и относительной позиции
     *
     * @param supRecordId                id записи СУП
     * @param position                   относительная позиция записи в СУП
     * @param itemsList                  List с параметрами и направлением сортировки
     * @param requestParams              входящие параметры get запроса
     * @param supObjectRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/suptable/{suprecordid}")
    @Operation(summary = "Получение записи СУП из таблицы по id",
            description = "Получение записи СУП из таблицы по id с параметрами для фильтрации")
    public TransportDTO<SupRecordDTO> getSupRecordByIdWithSort(
            @PathVariable(name = "suprecordid")
            @Parameter(description = "id записи", example = "100") Long supRecordId,
            @RequestParam(value = "position", required = false)
            @Parameter(hidden = true) String position,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(required = false, value = "sortby") List<String> itemsList,
            @ParameterObject SupObjectRequest supObjectRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParams) {

        SupObjectRequest supRequest = objectMapper.convertValue(requestParams, SupObjectRequest.class);
        log.debug("Sup request converted {}", supRequest);
        SupRecordDTO supRecordDTO = null;
        try {
            requestMapper.completeTheRecordRequestWithId(supRequest, itemsList, position);
            log.debug("The record request with id {} completed {}", supRecordId, supRequest);
            supRecordDTO = supService.getSupRecordByIdWithSort(supRecordId, supRequest);
            log.info("Method get sup record by id {} with sort is done", supRecordId);
            return baseTransportService.createSuccessfulTransportDTO(supRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get sup record by id {} with sort", supRecordId, ex);
            return baseTransportService.createErrorTransportDTO(supRecordDTO, ex);
        }
    }
}
