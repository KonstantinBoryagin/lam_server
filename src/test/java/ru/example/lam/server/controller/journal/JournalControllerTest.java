package ru.example.lam.server.controller.journal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_NOT_FOUND_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_TITLE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.SUCCESS_CODE;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.configuration.annotation.MockMvcTest;
import ru.example.lam.server.controller.MockMvcTestUtil;
import ru.example.lam.server.dto.journal.AddJournalDTO;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.dto.journal.enums.JournalMessageType;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.service.journal.JournalServiceStabImpl;
import ru.example.lam.server.service.mapping.DtoMapper;

/**
 * Тестовый класс для {@link JournalController}
 */
@MockMvcTest
@BootTest
class JournalControllerTest {
    private static final String PARENT_URL = "http://localhost:8080/api/journal";
    private static final String ADD_JOURNAL_URI = "/addjournal";
    private static final String JOURNAL_TABLE_URI = "/journaltable";
    private static final String JOURNAL_RECORD_ID_URI = "/{journalrecordid}";
    private static final String JOURNAL_TABLE_RECORD_ID_URI = "/journaltable/{journalrecordid}";
    private static final String PATH_TO_JSON_FILE = "classpath:json/journal/journalRecordWithIdRequest.json";
    private static final String ACTUAL_ERROR_MESSAGE = "The 'id' is less than 1";
    private static final String WRONG_ID_ERROR_MESSAGE = "Record with this id does not exist";
    private static ObjectMapper objectMapper;
    @Value("${AppSysName}")
    private String systemName;

    @Autowired
    private JournalServiceStabImpl journalService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DtoMapper dtoMapper;

    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("test for get journal table method with invalid request")
    void getJournalTableTest() throws Exception {
        String uri = PARENT_URL + JOURNAL_TABLE_URI;
        mockMvc
                .perform(MockMvcRequestBuilders.get(uri)
                        .param("datefrom", "30.32")
                        .param("subsystemname", "sub sys name")
                        .param("username", "username")
                        .param("messagetype", "")   //empty param
                        .param("host", "host value")
                )
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE));
    }

    @Test
    @DisplayName("test for get journal table with correct request")
    void getJournalTableFailTest() throws Exception {
        String uri = PARENT_URL + JOURNAL_TABLE_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .param("datefrom", "30.32")
                        .param("subsystemname", "sub sys name")
                        .param("username", "username")
                        .param("messagetype", "Info")
                        .param("host", "host value")
                )
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body[0].username").value("username")),
                        (jsonPath("$.body[0].subsystemname").value("sub sys name")),
                        (jsonPath("$.body[0].messagetype").value("Info")),
                        (jsonPath("$.body[0].host").value("host value")));
    }


    @Test
    @DisplayName("test for get journal record with invalid id")
    void getJournalRecordByIdFailTest() throws Exception {
        int requestId = -2;
        String url = PARENT_URL + JOURNAL_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(url, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get journal record with wrong id")
    void getJournalRecordByWrongIdTest() throws Exception {
        int requestId = 401;
        String url = PARENT_URL + JOURNAL_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(url, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, WRONG_ID_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get journal record with correct id")
    void getJournalRecordByIdTest() throws Exception {
        int requestId = 20;
        int expectedListSize = 1;
        String uri = PARENT_URL + JOURNAL_RECORD_ID_URI;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body[0].id").value(requestId)))
                .andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<JournalRecordDTO> journalRecordDTOS = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<JournalRecordDTO>>() {
        });
        assertEquals(expectedListSize, journalRecordDTOS.size());
        assertEquals(journalRecordDTOS.get(0), journalService.getJournalRecordDTOById());
    }

    @Test
    @DisplayName("test for delete journal table with invalid id")
    void deleteJournalRecordByInvalidIdTest() throws Exception {
        int requestId = -2;
        String uri = PARENT_URL + JOURNAL_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for delete journal table with correct id")
    void deleteJournalRecordByIdTest() throws Exception {
        int requestId = 20;
        String uri = PARENT_URL + JOURNAL_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)));
    }

    @Test
    @DisplayName("test for delete journal table with wrong id")
    void deleteJournalRecordByWrongIdTest() throws Exception {
        int requestId = 401;
        String uri = PARENT_URL + JOURNAL_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(false)),
                        (jsonPath("$.code").value(ERROR_NOT_FOUND_CODE)));
    }

    @Test
    @DisplayName("test for add journal with correct parameters")
    void addCorrectJournalTest() throws Exception {
        String uri = PARENT_URL + ADD_JOURNAL_URI;
        AddJournalDTO addJournal = prepareTestObject();
        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.postWithJson(uri, addJournal))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)))
                .andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<JournalRecordDTO> addJournalDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<JournalRecordDTO>>() {
        });
        JournalRecordDTO journalRecordDTO = addJournalDTOList.get(0);
        JournalRecordDTO expectedRecord = dtoMapper.fromAddToJournalRecordDto(prepareTestObject());
        assertEquals(expectedRecord, journalRecordDTO);
    }

    @Test
    @DisplayName("test for add journal without content")
    void addEmptyJournalTest() throws Exception {
        String uri = PARENT_URL + ADD_JOURNAL_URI;
        String expectedErrorMessage = "addJournalDTO is null";
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, expectedErrorMessage));
    }

    @Test
    @DisplayName("test for get journal record with id and position/filters")
    void getJournalRecordByIdWithFiltersTest() throws Exception {
        long requestId = 20L;
        String uri = PARENT_URL + JOURNAL_TABLE_RECORD_ID_URI;
        ObjectMapper objectMapperWithParam = new ObjectMapper();
        objectMapperWithParam.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        String pathToJson = ResourceUtils.getFile(PATH_TO_JSON_FILE).getPath();
        String jsonAsString = new String(Files.readAllBytes(Paths.get(pathToJson)), StandardCharsets.UTF_8);
        Map<String, List<String>> readValues = objectMapperWithParam.readValue(jsonAsString, new TypeReference<Map<String, List<String>>>() {
        });
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>(readValues);

        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId)
                        .params(requestParams))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body[0].id").value(requestId)),
                        (jsonPath("$.body[0].subsystemname").value("sub system name")),
                        (jsonPath("$.body[0].host").value("host value")),
                        (jsonPath("$.body[0].messagetype").value("Debug")),
                        (jsonPath("$.body[0].userid").value("5056445")),
                        (jsonPath("$.body[0].username").value("username")),
                        (jsonPath("$.body[0].datasource").value("datasource value")),
                        (jsonPath("$.body[0].method").value("method")),
                        (jsonPath("$.body[0].namespace").value("namespace")),
                        (jsonPath("$.body[0].pageoffset").value(25L)),
                        (jsonPath("$.body[0].version").value("2")),
                        (jsonPath("$.body[0].tags[0].name").value("1")),
                        (jsonPath("$.body[0].tags[0].value").value("one"))
                );
    }

    @Test
    @DisplayName("test for get journal record with invalid id")
    void getJournalRecordByInvalidIdWithFiltersTest() throws Exception {
        int requestId = -9;
        String uri = PARENT_URL + JOURNAL_TABLE_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get journal record with wrong id")
    void getJournalRecordByWrongIdWithFiltersTest() throws Exception {
        int requestId = 405;
        String uri = PARENT_URL + JOURNAL_TABLE_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, WRONG_ID_ERROR_MESSAGE));
    }

    /**
     * Формирует объект {@link AddJournalDTO}
     *
     * @return объект {@link AddJournalDTO}
     */
    private AddJournalDTO prepareTestObject() {
        return AddJournalDTO.builder()
                .subSystemName("sub system name")
                .host("host value")
                .journalMessageType(JournalMessageType.DEBUG)
                .username("username")
                .header("header and header")
                .userId("5056445")
                .message("message for server")
                .dataSource("datasource value")
                .message("method")
                .namespace("namespace")
                .request("request")
                .response("response")
                .eventTime(LocalDateTime.of(2022, 2, 15, 10, 25, 32))
                .version("1.0.0")
                .tagsDtoList(Arrays.asList(TagDTO.builder().name("1").value("first value").build(),
                        TagDTO.builder().name("2").value("second value").build(),
                        TagDTO.builder().name("3").value("third value").build()))
                .build();
    }
}
