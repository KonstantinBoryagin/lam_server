package ru.example.lam.server.controller.journal;

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

import java.util.List;
import java.util.Map;

import ru.example.lam.server.configuration.swagger.SwaggerConfig;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.aspect.Loggable;
import ru.example.lam.server.dto.journal.AddJournalDTO;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.journal.JournalTableDTO;
import ru.example.lam.server.model.journal.JournalTableRequest;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.service.journal.IJournalService;
import ru.example.lam.server.service.mapping.TableRequestMapper;
import ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl;

/**
 * RestController для обработки событий журнала
 */
@RestController
@RequestMapping(value = "/api/journal", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Journal", description = "Взаимодействие с журналом")
@Log4j2
public class JournalController {

    private IJournalService journalService;
    private BaseTransportServiceImpl baseTransportService;
    private TableRequestMapper tableRequestMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setJournalTableRequestMapper(TableRequestMapper tableRequestMapper) {
        this.tableRequestMapper = tableRequestMapper;
    }

    @Autowired
    public void setJournalService(IJournalService journalService) {
        this.journalService = journalService;
    }

    @Autowired
    public void setBaseTransportService(BaseTransportServiceImpl baseTransportService) {
        this.baseTransportService = baseTransportService;
    }

    /**
     * Получение таблицы журнала событий
     *
     * @param tagsList                      List тегов
     * @param itemsList                     List с параметрами и направлением сортировки
     * @param requestParams                 входящие параметры get запроса
     * @param journalTableRequestForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/journaltable")
    @Operation(description = "Получение таблицы журнала с входящими параметрами для фильтрации", summary = "Получение таблицы журнала")
    public TransportDTO<JournalTableDTO> getJournalTable(
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_TAGS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.TAGS_EXAMPLE)))
            @RequestParam(required = false, value = "tags") List<String> tagsList,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(required = false, value = "sortby") List<String> itemsList,
            @ParameterObject JournalTableRequest journalTableRequestForSwagger,
            @RequestParam(required = false) Map<String, String> requestParams) {

        JournalTableDTO journalTable = null;
        try {
            JournalTableRequest journalTableRequest = objectMapper.convertValue(requestParams, JournalTableRequest.class);
            log.debug("Journal table request converted {}", journalTableRequest);
            tableRequestMapper.completeTheTableRequest(journalTableRequest, tagsList, itemsList);
            log.debug("The table request completed {}", journalTableRequest);
            journalTable = journalService.getJournalTable(journalTableRequest);
            log.info("Method getJournalTable is done");
            return baseTransportService.createSuccessfulTransportDTO(journalTable);
        } catch (Exception ex) {
            log.error("Failed to get journal table", ex);
            return baseTransportService.createErrorTransportDTO(journalTable, ex);
        }
    }

    /**
     * Получение записи в журнале по id
     *
     * @param journalRecordId id записи в журнале
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/{journalrecordid}")
    @Operation(description = "Получение записи журнала по id", summary = "Получение записи журнала по id")
    public TransportDTO<JournalRecordDTO> getJournalRecordById(@PathVariable(name = "journalrecordid")
                                                               @Parameter(required = true, example = "100",
                                                                       description = "id записи в журнале")
                                                               Long journalRecordId) {
        JournalRecordDTO journalRecordById = null;
        try {
            journalRecordById = journalService.getJournalRecordById(journalRecordId);
            log.info("Method getJournalRecordById {} is done", journalRecordId);
            return baseTransportService.createSuccessfulTransportDTO(journalRecordById);
        } catch (Exception e) {
            log.error("Failed to get journal record by id {}", journalRecordId, e);
            return baseTransportService.createErrorTransportDTO(journalRecordById, e);
        }
    }


    /**
     * Удаление записи из журнала по id
     *
     * @param journalRecordId id записи для удаления
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{journalrecordid}")
    @Operation(description = "Удаление записи журнала по id", summary = "Удаление записи журнала по id")
    public TransportDTO<JournalRecordDTO> deleteJournalRecordById(@PathVariable(name = "journalrecordid")
                                                                  @Parameter(required = true, example = "100",
                                                                          description = "id записи в журнале")
                                                                  Long journalRecordId) {
        JournalRecordDTO journalRecordDTO = null;
        try {
            boolean result = journalService.deleteJournalRecordById(journalRecordId);
            log.debug("Method delete journal record by id {} is done", journalRecordId);
            if (result) {
                log.info("Method deleteJournalRecordById {} is {}", journalRecordId, result);
                return baseTransportService.createEmptySuccessfulTransportDTO(journalRecordDTO);
            } else {
                log.info("Method deleteJournalRecordById {} is {}", journalRecordId, result);
                return baseTransportService.createEmptyErrorTransportDTO(journalRecordDTO, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception ex) {
            log.error("Failed to delete journal record by id {}", journalRecordId, ex);
            return baseTransportService.createErrorTransportDTO(journalRecordDTO, ex);
        }
    }

    /**
     * Добавление записи в журнал
     *
     * @param addJournalDTO запись для добавления в журнал
     * @return {@link TransportDTO}
     */
    @Loggable
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/addjournal", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Добавление записи в журнал", summary = "Добавление записи в журнал")
    public TransportDTO<JournalRecordDTO> addJournal(@RequestBody(required = false) AddJournalDTO addJournalDTO) {
        JournalRecordDTO journalRecordDTO = null;
        try {
            journalRecordDTO = journalService.addJournal(addJournalDTO);
            log.info("Method addJournal {} is done", addJournalDTO);
            return baseTransportService.createSuccessfulTransportDTO(journalRecordDTO);
        } catch (Exception e) {
            log.error("Failed to add journal {}", addJournalDTO, e);
            return baseTransportService.createErrorTransportDTO(journalRecordDTO, e);
        }
    }

    /**
     * Получение записи в журнале по id, фильтру, относительной позиции
     *
     * @param journalRecordId                      id записи в журнале
     * @param position                             относительная позиция записи в журнале
     * @param tagsList                             List тегов
     * @param itemsList                            List с параметрами и направлением сортировки
     * @param requestParams                        входящие параметры get запроса
     * @param journalRecordRequestWithIdForSwagger используется для Swagger документации
     * @return {@link TransportDTO}
     * @throws LamServerException {@link LamServerException} исключение с описанием причины
     */
    @Loggable
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping(value = "/journaltable/{journalrecordid}")
    @Operation(description = "Получение записи журнала из таблицы по id с параметрами для фильтрации",
            summary = "Получение записи журнала из таблицы по id")
    public TransportDTO<JournalRecordDTO> getJournalRecordByIdWithFilters(
            @PathVariable(name = "journalrecordid")
            @Parameter(required = true, example = "100", description = "id записи в журнале") Long journalRecordId,
            @RequestParam(required = false, value = "position") @Parameter(name = "position", hidden = true) String position,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_TAGS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.TAGS_EXAMPLE)))
            @RequestParam(required = false, value = "tags") List<String> tagsList,
            @Parameter(description = SwaggerConfig.DESCRIPTION_FOR_ITEMS_LIST,
                    array = @ArraySchema(schema = @Schema(type = "string", example = SwaggerConfig.ITEMS_EXAMPLE)))
            @RequestParam(required = false, value = "sortby") List<String> itemsList,
            @ParameterObject JournalRecordRequestWithId journalRecordRequestWithIdForSwagger,
            @RequestParam(required = false) Map<String, String> requestParams) throws LamServerException {

        JournalRecordRequestWithId journalRecordRequestWithId = objectMapper.convertValue(requestParams, JournalRecordRequestWithId.class);
        log.debug("Journal record request with id {} converted {}", journalRecordId, journalRecordRequestWithId);
        tableRequestMapper.completeTheRecordRequestWithId(journalRecordRequestWithId,
                tagsList, itemsList, position);
        log.debug("The record request with id {} completed {}", journalRecordId, journalRecordRequestWithId);
        JournalRecordDTO journalRecordDTO = null;
        try {
            journalRecordDTO = journalService.getJournalRecordByIdWithFilters(journalRecordId, journalRecordRequestWithId);
            log.info("Method get journal record by id {} with filters is done", journalRecordId);
            return baseTransportService.createSuccessfulTransportDTO(journalRecordDTO);
        } catch (Exception ex) {
            log.error("Failed to get journal record by id {} with filters", journalRecordId, ex);
            return baseTransportService.createErrorTransportDTO(journalRecordDTO, ex);
        }
    }
}
