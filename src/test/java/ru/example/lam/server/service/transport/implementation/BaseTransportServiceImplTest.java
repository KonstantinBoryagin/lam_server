package ru.example.lam.server.service.transport.implementation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_TITLE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.SUCCESS_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_NOT_FOUND_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_BAD_REQUEST_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.MESSAGE_TITLE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.MESSAGE_WRONG_ROLE_MESSAGE;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.dto.journal.enums.JournalMessageType;
import ru.example.lam.server.coreserver.transport.TransportDTO;

/**
 * Тестовый класс для {@link BaseTransportServiceImpl}
 */
@BootTest
class BaseTransportServiceImplTest {
    private static final String EXCEPTION_MESSAGE = "Exception for test";
    private static JournalRecordDTO journalRecordDTO;
    private static final Integer EXPECTED_LIST_SIZE = 1;
    private static LamServerException exception;
    @Value("${AppSysName}")
    private String systemName;
    @Autowired
    private BaseTransportServiceImpl baseTransportService;

    @BeforeAll
    public static void init() {
        journalRecordDTO = JournalRecordDTO.builder()
                .id(55L)
                .dateTime(LocalDateTime.of(2022, 5, 15, 23, 50, 10))
                .subSystemName("system name")
                .host("innopolis.ru")
                .journalMessageType(JournalMessageType.WARNING)
                .username("admin")
                .header("top")
                .userId("001")
                .tagsDtoList(Arrays.asList(TagDTO.builder().name("tag_1").value("value_1").build(),
                        TagDTO.builder().name("tag_2").value("value_2").build()))
                .message("all is fine")
                .dataSource("datasource")
                .method("method")
                .request("request")
                .response("response")
                .version("1.0.0")
                .build();

        exception = new LamServerException(EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("test for create successful TransportDTO")
    void createSuccessfulTransportDTOTest() {
        TransportDTO<JournalRecordDTO> successfulTransportDTO = baseTransportService.createSuccessfulTransportDTO(journalRecordDTO);
        assertEquals(EXPECTED_LIST_SIZE, successfulTransportDTO.getBody().size());
        assertEquals(SUCCESS_CODE, successfulTransportDTO.getCode());
        assertEquals(true, successfulTransportDTO.getSuccess());
        assertEquals(journalRecordDTO, successfulTransportDTO.getBody().get(0));
    }

    @Test
    @DisplayName("test for create empty successful TransportDTO")
    void createEmptySuccessfulTransportDTOTest() {
        TransportDTO<JournalRecordDTO> emptySuccessfulTransportDTO = baseTransportService.createEmptySuccessfulTransportDTO(journalRecordDTO);
        assertEquals(SUCCESS_CODE, emptySuccessfulTransportDTO.getCode());
        assertEquals(true, emptySuccessfulTransportDTO.getSuccess());
    }

    @Test
    @DisplayName("test for create successful TransportDTO with message")
    void createSuccessfulTransportDTOWithMessageTest() {
        List<String> messages = new ArrayList<>(Arrays.asList("admin43", "user32"));
        TransportDTO<JournalRecordDTO> transportDTOWithMessage = baseTransportService.createSuccessfulTransportDTOWithMessage(messages, journalRecordDTO);
        assertEquals(2, transportDTOWithMessage.getMessage().size());
        assertEquals(true, transportDTOWithMessage.getSuccess());
        assertEquals(SUCCESS_CODE, transportDTOWithMessage.getCode());
        assertEquals(EXPECTED_LIST_SIZE, transportDTOWithMessage.getBody().size());
        assertEquals(MESSAGE_TITLE, transportDTOWithMessage.getMessage().get(0).getTitle());
        assertEquals(MESSAGE_WRONG_ROLE_MESSAGE, transportDTOWithMessage.getMessage().get(0).getMessage());
    }

    @Test
    @DisplayName("test for create error TransportDTO")
    void createErrorTransportDTOTest() {
        TransportDTO<JournalRecordDTO> errorTransportDTO = baseTransportService.createErrorTransportDTO(journalRecordDTO, exception);
        assertEquals(EXPECTED_LIST_SIZE, errorTransportDTO.getErrors().size());
        assertEquals(false, errorTransportDTO.getSuccess());
        assertEquals(ERROR_NOT_FOUND_CODE, errorTransportDTO.getCode());
        assertEquals(ERROR_NOT_FOUND_CODE, errorTransportDTO.getErrors().get(0).getCode());
        assertEquals(systemName, errorTransportDTO.getErrors().get(0).getSystem());
        assertEquals(ERROR_TITLE, errorTransportDTO.getErrors().get(0).getTitle());
        assertEquals(EXCEPTION_MESSAGE, errorTransportDTO.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("test for create TransportDTO with cause")
    void createErrorTransportDTOWithCauseTest() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("test", new DataIntegrityViolationException("test", new DataIntegrityViolationException("test")));
        TransportDTO<JournalRecordDTO> errorTransportDTOWithCause = baseTransportService.createErrorTransportDTOWithCause(journalRecordDTO, exception);
        assertNotNull(errorTransportDTOWithCause);
        assertEquals(false, errorTransportDTOWithCause.getSuccess());
        assertEquals(ERROR_BAD_REQUEST_CODE, errorTransportDTOWithCause.getCode());
        assertEquals(EXPECTED_LIST_SIZE, errorTransportDTOWithCause.getErrors().size());
    }

    @Test
    @DisplayName("test for create error TransportDTO with received error code")
    void createErrorTransportDTOWithErrorCodeTest() {
        int errorCode = HttpStatus.BAD_REQUEST.value();
        TransportDTO<JournalRecordDTO> errorTransportDTO = baseTransportService.createErrorTransportDTO(journalRecordDTO,
                errorCode, exception);
        assertEquals(EXPECTED_LIST_SIZE, errorTransportDTO.getErrors().size());
        assertEquals(false, errorTransportDTO.getSuccess());
        assertEquals(errorCode, errorTransportDTO.getCode());
        assertEquals(errorCode, errorTransportDTO.getErrors().get(0).getCode());
        assertEquals(systemName, errorTransportDTO.getErrors().get(0).getSystem());
        assertEquals(ERROR_TITLE, errorTransportDTO.getErrors().get(0).getTitle());
        assertEquals(EXCEPTION_MESSAGE, errorTransportDTO.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("test for create error TransportDTO with received error code and system name")
    void createErrorTransportDTOWithErrorCodeAndTitleTest() {
        int errorCode = HttpStatus.BAD_REQUEST.value();
        String systemName = "LAM";
        TransportDTO<JournalRecordDTO> errorTransportDTO = baseTransportService.createErrorTransportDTO(journalRecordDTO,
                systemName, errorCode, exception);
        assertEquals(EXPECTED_LIST_SIZE, errorTransportDTO.getErrors().size());
        assertEquals(false, errorTransportDTO.getSuccess());
        assertEquals(errorCode, errorTransportDTO.getCode());
        assertEquals(errorCode, errorTransportDTO.getErrors().get(0).getCode());
        assertEquals(systemName, errorTransportDTO.getErrors().get(0).getSystem());
        assertEquals(ERROR_TITLE, errorTransportDTO.getErrors().get(0).getTitle());
        assertEquals(EXCEPTION_MESSAGE, errorTransportDTO.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("test for create error TransportDTO with received error code and system name, and title")
    void createErrorTransportDTOWithErrorCodeAndTitleAndTitleTest() {
        int errorCode = HttpStatus.BAD_REQUEST.value();
        String systemName = "LAM";
        String title = "error in system";
        TransportDTO<JournalRecordDTO> errorTransportDTO = baseTransportService.createErrorTransportDTO(journalRecordDTO,
                systemName, title, errorCode, exception);
        assertEquals(EXPECTED_LIST_SIZE, errorTransportDTO.getErrors().size());
        assertEquals(false, errorTransportDTO.getSuccess());
        assertEquals(errorCode, errorTransportDTO.getCode());
        assertEquals(errorCode, errorTransportDTO.getErrors().get(0).getCode());
        assertEquals(systemName, errorTransportDTO.getErrors().get(0).getSystem());
        assertEquals(title, errorTransportDTO.getErrors().get(0).getTitle());
        assertEquals(EXCEPTION_MESSAGE, errorTransportDTO.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("test for create empty error TransportDTO")
    void createEmptyErrorTransportDTO() {
        int errorCode = HttpStatus.FORBIDDEN.value();
        TransportDTO<JournalRecordDTO> emptyErrorTransportDTO = baseTransportService.createEmptyErrorTransportDTO(journalRecordDTO,
                errorCode);
        assertEquals(false, emptyErrorTransportDTO.getSuccess());
        assertEquals(errorCode, emptyErrorTransportDTO.getCode());
    }

}