package ru.example.lam.server.controller.audit;

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
import ru.example.lam.server.dto.audit.AddAuditDTO;
import ru.example.lam.server.dto.audit.AuditRecordDTO;
import ru.example.lam.server.dto.audit.enums.AuditMessageType;
import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.service.audit.AuditServiceStabImpl;
import ru.example.lam.server.service.mapping.DtoMapper;

/**
 * Тестовый класс для {@link AuditController}
 */
@MockMvcTest
@BootTest
class AuditControllerTest {
    private static final String PARENT_URL = "http://localhost:8080/api/audit";
    private static final String ADD_AUDIT_URI = "/addaudit";
    private static final String AUDIT_TABLE_URI = "/audittable";
    private static final String AUDIT_RECORD_ID_URI = "/{auditrecordid}";
    private static final String AUDIT_TABLE_RECORD_ID_URI = "/audittable/{auditrecordid}";
    private static final String PATH_TO_JSON_FILE = "classpath:json/audit/auditRecordWithIdRequest.json";
    private static final String ACTUAL_ERROR_MESSAGE = "The 'id' is less than 1";
    private static final String WRONG_ID_ERROR_MESSAGE = "Record with this id does not exist";
    private static ObjectMapper objectMapper;
    @Value("${AppSysName}")
    private String systemName;

    @Autowired
    private AuditServiceStabImpl auditServiceStab;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DtoMapper dtoMapper;

    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("test for get audit table method with invalid request")
    void getAuditTableTest() throws Exception {
        String uri = PARENT_URL + AUDIT_TABLE_URI;
        mockMvc
                .perform(MockMvcRequestBuilders.get(uri)
                        .param("datefrom", "10.12.2022")
                        .param("subsystemname", "sub sys name")
                        .param("username", "admin")
                        .param("messagetype", "")   //empty param in enum
                        .param("host", "host")
                )
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE));
    }

    @Test
    @DisplayName("test for get audit table with correct request")
    void getAuditTableFailTest() throws Exception {
        String uri = PARENT_URL + AUDIT_TABLE_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .param("datefrom", "10.10.2010")
                        .param("subsystemname", "sub system name")
                        .param("username", "admin")
                        .param("messagetype", "Create")
                        .param("host", "host source")
                )
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body[0].username").value("admin")),
                        (jsonPath("$.body[0].subsystemname").value("sub system name")),
                        (jsonPath("$.body[0].messagetype").value("Create")),
                        (jsonPath("$.body[0].host").value("host source")));
    }

    @Test
    @DisplayName("test for get audit record with invalid id")
    void getAuditRecordByIdFailTest() throws Exception {
        int requestId = -2;
        String url = PARENT_URL + AUDIT_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(url, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get audit record with wrong id")
    void getAuditRecordByWrongIdTest() throws Exception {
        int requestId = 401;
        String url = PARENT_URL + AUDIT_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(url, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, WRONG_ID_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get audit record with correct id")
    void getAuditRecordByIdTest() throws Exception {
        int requestId = 20;
        int expectedListSize = 1;
        String uri = PARENT_URL + AUDIT_RECORD_ID_URI;
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
        List<AuditRecordDTO> auditRecordDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<AuditRecordDTO>>() {
        });
        assertEquals(expectedListSize, auditRecordDTOList.size());
        assertEquals(auditRecordDTOList.get(0), auditServiceStab.getAuditRecordDTO());
    }

    @Test
    @DisplayName("test for delete audit table with invalid id")
    void deleteAuditRecordByInvalidIdTest() throws Exception {
        int requestId = -2;
        String uri = PARENT_URL + AUDIT_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for delete audit table with correct id")
    void deleteAuditRecordByIdTest() throws Exception {
        int requestId = 20;
        String uri = PARENT_URL + AUDIT_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)));
    }

    @Test
    @DisplayName("test for delete audit table with wrong id")
    void deleteAuditRecordByWrongIdTest() throws Exception {
        int requestId = 401;
        String uri = PARENT_URL + AUDIT_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(false)),
                        (jsonPath("$.code").value(ERROR_NOT_FOUND_CODE)));
    }

    @Test
    @DisplayName("test for add audit with correct parameters")
    void addCorrectAuditTest() throws Exception {
        String uri = PARENT_URL + ADD_AUDIT_URI;
        AddAuditDTO addAudit = prepareTestObject();
        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.postWithJson(uri, addAudit))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)))
                .andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<AuditRecordDTO> addAuditDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<AuditRecordDTO>>() {
        });
        AuditRecordDTO auditRecordDTO = addAuditDTOList.get(0);
        AuditRecordDTO expectedRecord = dtoMapper.fromAddToAuditRecordDto(prepareTestObject());
        assertEquals(expectedRecord, auditRecordDTO);
    }

    @Test
    @DisplayName("test for add audit without content")
    void addEmptyAuditTest() throws Exception {
        String uri = PARENT_URL + ADD_AUDIT_URI;
        String expectedErrorMessage = "addAuditDTO is null";
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, expectedErrorMessage));
    }

    @Test
    @DisplayName("test for get audit record with id and position/filters")
    void getAuditRecordByIdWithFiltersTest() throws Exception {
        long requestId = 20L;
        String uri = PARENT_URL + AUDIT_TABLE_RECORD_ID_URI;
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
                        (jsonPath("$.body[0].host").value("host source")),
                        (jsonPath("$.body[0].messagetype").value("Update")),
                        (jsonPath("$.body[0].userid").value("1000256445")),
                        (jsonPath("$.body[0].username").value("admin")),
                        (jsonPath("$.body[0].datasource").value("datasource value")),
                        (jsonPath("$.body[0].method").value("delete")),
                        (jsonPath("$.body[0].namespace").value("namespace")),
                        (jsonPath("$.body[0].pageoffset").value(23L)),
                        (jsonPath("$.body[0].version").value("2.0.0")),
                        (jsonPath("$.body[0].tags[0].name").value("1.1")),
                        (jsonPath("$.body[0].tags[0].value").value("one"))
                );
    }

    @Test
    @DisplayName("test for get audit record with invalid id")
    void getAuditRecordByInvalidIdWithFiltersTest() throws Exception {
        int requestId = -9;
        String uri = PARENT_URL + AUDIT_TABLE_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get audit record with wrong id")
    void getAuditRecordByWrongIdWithFiltersTest() throws Exception {
        int requestId = 405;
        String uri = PARENT_URL + AUDIT_TABLE_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, WRONG_ID_ERROR_MESSAGE));
    }

    /**
     * Формирует объект {@link AddAuditDTO}
     *
     * @return объект {@link AddAuditDTO}
     */
    private AddAuditDTO prepareTestObject() {
        return AddAuditDTO.builder()
                .subSystemName("sub system name")
                .host("host source")
                .auditMessageType(AuditMessageType.EXTRACT)
                .username("admin")
                .header("header value")
                .userId("100025554")
                .message("message for server")
                .dataSource("datasource source")
                .message("post")
                .namespace("namespaces")
                .request("request")
                .response("response")
                .eventTime(LocalDateTime.of(2022, 2, 15, 10, 25, 32))
                .version("1.0.1")
                .tagsDtoList(Arrays.asList(TagDTO.builder().name("01").value("first value").build(),
                        TagDTO.builder().name("02").value("second value").build(),
                        TagDTO.builder().name("03").value("third value").build()))
                .build();
    }

}