package ru.example.lam.server.controller.feature;

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
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.dto.feature.CreateFeatureDTO;
import ru.example.lam.server.dto.feature.EditFeatureDTO;
import ru.example.lam.server.dto.feature.FeatureRecordDTO;
import ru.example.lam.server.dto.feature.FeatureTableDTO;
import ru.example.lam.server.aspect.Loggable;
import ru.example.lam.server.model.feature.FeatureRequest;
import ru.example.lam.server.service.feature.IFeatureService;
import ru.example.lam.server.service.mapping.TableRequestMapper;
import ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl;

/**
 * REST Controller для обработки запросов к Feature
 */
@RestController
@RequestMapping(value = "/api/feature/", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Feature", description = "Взаимодействие с Feature")
@Log4j2
public class FeatureController {
    private IFeatureService featureService;
    private BaseTransportServiceImpl baseTransportService;
    private TableRequestMapper requestMapper;
    private ObjectMapper objectMapper;

    @Autowired
    public void setFeatureService(IFeatureService featureService) {
        this.featureService = featureService;
    }

    @Autowired
    public void setBaseTransportService(BaseTransportServiceImpl baseTransportService) {
        this.baseTransportService = baseTransportService;
    }

    @Autowired
    public void setRequestMapper(TableRequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Получение таблицы Feature
     *
     * @param itemsList                List с параметрами и направлением сортировки
     * @param requestParam             входящие параметры get запроса
     * @param featureRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/featuretable")
    @Operation(summary = "Получение таблицы Feature",
            description = "Получение таблицы Feature с входящими параметрами для фильтрации")
    public TransportDTO<FeatureTableDTO> getFeatureTable(
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(value = "sortby", required = false) List<String> itemsList,
            @ParameterObject FeatureRequest featureRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParam) {

        FeatureTableDTO featureTableDTO = null;
        try {
            FeatureRequest featureRequest = objectMapper.convertValue(requestParam, FeatureRequest.class);
            log.debug("Feature request converted {}", featureRequest);
            requestMapper.completeTheTableRequest(featureRequest, itemsList);
            log.debug("The table request completed {}", featureRequest);
            featureTableDTO = featureService.getFeatureTable(featureRequest);
            log.info("Method getFeatureTable is done");
            return baseTransportService.createSuccessfulTransportDTO(featureTableDTO);
        } catch (Exception ex) {
            log.error("Failed to get feature table", ex);
            return baseTransportService.createErrorTransportDTO(featureTableDTO, ex);
        }
    }

    /**
     * Получение записи Feature по id
     *
     * @param featureRecordId id записи в Feature
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{featurerecordid}")
    @Operation(summary = "Получение записи Feature по id", description = "Получение записи Feature по id")
    public TransportDTO<FeatureRecordDTO> getFeatureRecordById(
            @PathVariable(name = "featurerecordid")
            @Parameter(required = true, example = "100", description = "id записи Feature") Long featureRecordId) {

        FeatureRecordDTO featureRecordDTO = null;
        try {
            featureRecordDTO = featureService.getFeatureRecordById(featureRecordId);
            log.info("Method getFeatureRecordById {} is done", featureRecordId);
            return baseTransportService.createSuccessfulTransportDTO(featureRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get feature record by id {} featureRecordId", featureRecordId);
            return baseTransportService.createErrorTransportDTO(featureRecordDTO, ex);
        }
    }

    /**
     * Удаление записи Feature по id
     *
     * @param featureRecordId id записи в Feature
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{featurerecordid}")
    @Operation(summary = "Удаление записи Feature по id", description = "Удаление записи Feature по id")
    public TransportDTO<FeatureRecordDTO> deleteFeatureRecordById(
            @PathVariable(name = "featurerecordid")
            @Parameter(required = true, example = "100", description = "id записи Feature") Long featureRecordId) {

        FeatureRecordDTO featureRecordDTO = new FeatureRecordDTO();
        try {
            boolean result = featureService.deleteFeatureRecordById(featureRecordId);
            log.debug("Method delete feature record by id {} is done", featureRecordId);
            if (result) {
                log.info("Method deleteFeatureRecordById {} is {}", featureRecordId, result);
                return baseTransportService.createEmptySuccessfulTransportDTO(featureRecordDTO);
            } else {
                log.info("Method deleteFeatureRecordById {} is {}", featureRecordId, result);
                return baseTransportService.createEmptyErrorTransportDTO(featureRecordDTO, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception ex) {
            log.error("Failed to delete feature record by id {}", featureRecordId, ex);
            return baseTransportService.createErrorTransportDTO(featureRecordDTO, ex);
        }
    }

    /**
     * Получение записи Feature по наименованию, версии, подсистеме
     *
     * @param featureName   имя записи
     * @param subsystemName подсистема записи
     * @param version       версия записи
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{featurename}/{subsystemname}/{version}")
    @Operation(summary = "Получение записи Feature по наименованию, версии, подсистеме",
            description = "Получение записи Feature по наименованию, версии, подсистеме")
    public TransportDTO<FeatureRecordDTO> getFeatureRecordByNameAndSubSystemAndVersion(
            @PathVariable(name = "featurename")
            @Parameter(description = "имя feature", example = "bug fix") String featureName,
            @PathVariable(name = "subsystemname")
            @Parameter(description = "подсистема", example = "LAM CORE") String subsystemName,
            @PathVariable(name = "version")
            @Parameter(description = "версия", example = "1.0.2 RELEASE") String version) {
        FeatureRecordDTO featureRecordDTO = null;
        try {
            featureRecordDTO = featureService.getFeatureRecordByNameAndSubSystemAndVersion(featureName,
                    subsystemName, version);
            log.info("Method get feature record by name {}, sub system {} and version {} is done", featureName, subsystemName, version);
            return baseTransportService.createSuccessfulTransportDTO(featureRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get feature record by name {}, sub system {} and version {}", featureName,subsystemName, version, ex);
            return baseTransportService.createErrorTransportDTO(featureRecordDTO, ex);
        }
    }

    /**
     * Добавить запись в Feature
     *
     * @param createFeatureDTO объект для добавлений в таблицу Feature
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/createfeature")
    @Operation(summary = "Создание записи Feature", description = "Создание записи Feature")
    public TransportDTO<FeatureRecordDTO> createFeatureRecord(@RequestBody(required = false) CreateFeatureDTO createFeatureDTO) {
        FeatureRecordDTO featureRecordDTO = null;
        try {
            featureRecordDTO = featureService.createFeatureRecord(createFeatureDTO);
            log.info("Method createFeatureRecord {} is done", createFeatureDTO);
            return baseTransportService.createSuccessfulTransportDTO(featureRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to create feature record {}", createFeatureDTO, ex);
            return baseTransportService.createErrorTransportDTO(featureRecordDTO, ex);
        }
    }

    /**
     * Изменить запись Feature
     *
     * @param editFeatureDTO объект содержащий параметры для изменения записи Feature
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/editfeature")
    @Operation(summary = "Изменение записи Feature", description = "Изменение записи Feature")
    public TransportDTO<FeatureRecordDTO> editFeatureRecord(@RequestBody(required = false) EditFeatureDTO editFeatureDTO) {
        FeatureRecordDTO featureRecordDTO = null;
        try {
            featureRecordDTO = featureService.editFeatureRecord(editFeatureDTO);
            log.info("Method edit feature record {} is done", editFeatureDTO);
            return baseTransportService.createSuccessfulTransportDTO(featureRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to edit feature record {}", editFeatureDTO, ex);
            return baseTransportService.createErrorTransportDTO(featureRecordDTO, ex);
        }
    }

    /**
     * Получение записи из таблицы Feature по id и фильтру
     *
     * @param featureRecordId          id записи в Feature
     * @param itemsList                List с параметрами и направлением сортировки
     * @param requestParam             входящие параметры get запроса
     * @param featureRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/featuretable/{featurerecordid}")
    @Operation(summary = "Получение записи Feature из таблицы",
            description = "Получение записи Feature из таблицы с входящими параметрами для фильтрации")
    public TransportDTO<FeatureRecordDTO> getFeatureRecordFromTable(
            @PathVariable(name = "featurerecordid")
            @Parameter(required = true, example = "100", description = "id записи Feature") Long featureRecordId,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(value = "sortby", required = false) List<String> itemsList,
            @ParameterObject FeatureRequest featureRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParam) {

        FeatureRecordDTO featureRecordDTO = null;
        try {
            FeatureRequest featureRequest = objectMapper.convertValue(requestParam, FeatureRequest.class);
            log.debug("Feature request converted {}", featureRequest);
            requestMapper.completeTheTableRequest(featureRequest, itemsList);
            log.debug("The table request completed {}", featureRequest);
            featureRecordDTO = featureService.getFeatureRecordFromTable(featureRecordId, featureRequest);
            log.info("Method getFeatureRecordFromTable by id {} is done", featureRecordId);
            return baseTransportService.createSuccessfulTransportDTO(featureRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get feature record from table by id {}", featureRecordId, ex);
            return baseTransportService.createErrorTransportDTO(featureRecordDTO, ex);
        }
    }
}
