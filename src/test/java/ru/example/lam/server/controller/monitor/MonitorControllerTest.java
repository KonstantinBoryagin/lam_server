package ru.example.lam.server.controller.monitor;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.configuration.annotation.MockMvcTest;
import ru.example.lam.server.controller.MockMvcTestUtil;
import ru.example.lam.server.dto.monitor.AddMonitorDTO;
import ru.example.lam.server.dto.monitor.MonitorRecordDTO;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.dto.monitor.MonitorTableDTO;
import ru.example.lam.server.service.mapping.DtoMapper;
import ru.example.lam.server.service.monitor.MonitorServiceStubImpl;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.example.lam.server.service.monitor.MonitorServiceStubImpl.NOT_FOUND_RECORD_MESSAGE;
import static ru.example.lam.server.service.monitor.MonitorServiceStubImpl.INVALID_ID_MESSAGE;
import static ru.example.lam.server.service.monitor.MonitorServiceStubImpl.EMPTY_REQUEST_BODY_MESSAGE;
import static ru.example.lam.server.service.monitor.MonitorServiceStubImpl.WRONG_VERSION_MESSAGE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.SUCCESS_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_TITLE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_NOT_FOUND_CODE;


/**
 * Тестовый класс для {@link MonitorController}
 */
@MockMvcTest
@BootTest
@DisplayName("Tests for MonitorController")
class MonitorControllerTest {
    private static final String PARENT_URL = "http://localhost:8080/api/monitor";
    private static final String TABLE_URI = "/monitortable";
    private static final String RECORD_ID_URI = "/{monitorrecordid}";
    private static final String SERVICE_VERSION_URI = "/{servicename}/{version}";
    private static final String ADD_URI = "/addmonitor";
    private static final String CHART_URI = "/monitorchart";
    private static final String HIDE_URI = "/hidemonitor";
    private static final int BODY_LIST_SIZE = 1;
    private static final String PATH_TO_JSON_FILE = "classpath:json/monitor/monitorRequestParams.json";

    @Value("${AppSysName}")
    private String systemName;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DtoMapper dtoMapper;
    @Autowired
    private MonitorServiceStubImpl monitorServiceStub;
    private static AddMonitorDTO addMonitorDTO;

    @BeforeAll
    static void init() {
        addMonitorDTO = AddMonitorDTO.builder()
                .serviceName("name of service")
                .serviceAddress("address of service")
                .version("10.0.0 RELEASE")
                .build();
    }

    @Test
    @DisplayName("test for get monitor table with correct request")
    void getMonitorTable() throws Exception {
        String uri = PARENT_URL + TABLE_URI;
        MultiValueMap<String, String> requestParams = getMapWithRequestParamsFromJson();
        mockMvc.perform(get(uri)
                        .params(requestParams))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE))),
                        (jsonPath("$.body[0].servicename").value("service name")),
                        (jsonPath("$.body[0].serviceaddress").value("service address")),
                        (jsonPath("$.body[0].status").value("Error")));
    }

    @Test
    @DisplayName("test for get monitor table with invalid request")
    void getSupTableFailTest() throws Exception {
        String uri = PARENT_URL + TABLE_URI;
        mockMvc.perform(get(uri)
                        .param("status", "")) //empty param in enum
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE));
    }

    @Test
    @DisplayName("test for get monitor record with correct id")
    void getMonitorRecordByIdTest() throws Exception {
        Long requestId = 269L;
        String uri = PARENT_URL + RECORD_ID_URI;
        MvcResult mvcResult = mockMvc.perform(get(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE))),
                        (jsonPath("$.body[0].id").value(requestId)))
                .andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<MonitorRecordDTO> monitorRecordDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<MonitorRecordDTO>>() {
        });
        assertEquals(monitorRecordDTOList.get(0), monitorServiceStub.getTestMonitorRecordDTO(requestId));
    }

    @Test
    @DisplayName("test for get monitor record with invalid id")
    void getMonitorRecordByIdFailTest() throws Exception {
        int requestId = -20;
        String uri = PARENT_URL + RECORD_ID_URI;
        mockMvc.perform(get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for get monitor record with wrong id")
    void getMonitorRecordByWrongIdTest() throws Exception {
        int requestId = 500;
        String uri = PARENT_URL + RECORD_ID_URI;
        mockMvc.perform(get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, requestId)));
    }

    @Test
    @DisplayName("test for delete monitor record with correct id")
    void deleteMonitorRecordByIdTest() throws Exception {
        int requestId = 20;
        String uri = PARENT_URL + RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)));
    }

    @Test
    @DisplayName("test for delete monitor record with invalid id")
    void deleteMonitorRecordByInvalidIdTest() throws Exception {
        int requestId = -2;
        String uri = PARENT_URL + RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for delete monitor record with wrong id")
    void deleteMonitorRecordByWrongIdTest() throws Exception {
        int requestId = 401;
        String uri = PARENT_URL + RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(false)),
                        (jsonPath("$.code").value(ERROR_NOT_FOUND_CODE)));
    }

    @Test
    @DisplayName("test for get monitor record with correct serviceName and version")
    void getMonitorRecordByServiceNameAndVersionTest() throws Exception {
        String uri = PARENT_URL + SERVICE_VERSION_URI;
        String serviceName = "app.subsys1";
        String version = "10";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri, serviceName, version))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE))))
                .andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<MonitorRecordDTO> auditRecordDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<MonitorRecordDTO>>() {
        });
        assertEquals(auditRecordDTOList.get(0), monitorServiceStub.getTestMonitorRecordDTO(Long.valueOf(version)));
    }

    @Test
    @DisplayName("test for get monitor record with empty version")
    void getMonitorRecordByServiceNameAndVersionFailTest() throws Exception {
        String uri = PARENT_URL + SERVICE_VERSION_URI;
        String serviceName = "app.subsys1";
        String version = "1000";
        mockMvc.perform(MockMvcRequestBuilders.get(uri, serviceName, version))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        WRONG_VERSION_MESSAGE));
    }

    @Test
    @DisplayName("test for add monitor record")
    void addMonitorRecordTest() throws Exception {
        String uri = PARENT_URL + ADD_URI;
        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.postWithJson(uri, addMonitorDTO))
                .andExpectAll(
                        (status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE)))
                ).andReturn();
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<MonitorTableDTO> supRecordDTOList = objectMapper.convertValue(transportDTO.getBody(),
                new TypeReference<List<MonitorTableDTO>>() {
                });
        MonitorTableDTO expectedMonitorRecord = dtoMapper.fromCreateToMonitorRecordDto(addMonitorDTO);
        assertEquals(expectedMonitorRecord, supRecordDTOList.get(0));

    }

    @Test
    @DisplayName("test for add monitor record with empty requestBody")
    void addMonitorRecordEmptyRequestBodyTest() throws Exception {
        String uri = PARENT_URL + ADD_URI;
        mockMvc.perform(MockMvcTestUtil.postWithJson(uri, null))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE, EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for edit monitor record")
    void putRecordToMonitorChartTest() throws Exception {
        String uri = PARENT_URL + CHART_URI + RECORD_ID_URI;
        long id = 150L;
        String requestAsString = objectMapper.writeValueAsString(monitorServiceStub.getTestMonitorChartDTO(id));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpectAll(
                        (status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE)))
                ).andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<MonitorRecordDTO> supRecordDTOList = objectMapper.convertValue(transportDTO.getBody(),
                new TypeReference<List<MonitorRecordDTO>>() {
                });
        MonitorRecordDTO expectedObject = monitorServiceStub.getMonitorRecordDTO();
        assertEquals(expectedObject, supRecordDTOList.get(0));
    }

    @Test
    @DisplayName("test for edit monitor record with invalid id")
    void putRecordToMonitorChartWithInvalidIdTest() throws Exception {
        String uri = PARENT_URL + CHART_URI + RECORD_ID_URI;
        long id = 500L;
        String requestAsString = objectMapper.writeValueAsString(monitorServiceStub.getTestMonitorChartDTO(id));
        mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, id)));
    }

    @Test
    @DisplayName("test for edit monitor record with empty request body")
    void putRecordToMonitorChartWithEmptyRequestBodyTest() throws Exception {
        String uri = PARENT_URL + CHART_URI + RECORD_ID_URI;
        long id = 55L;
        mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE, EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for hide monitor record with hide")
    void hideOrShowMonitorRecordTest() throws Exception {
        String uri = PARENT_URL + HIDE_URI + RECORD_ID_URI;
        long id = 55L;
        boolean hide = true;
        mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("hide", String.valueOf(hide)))
                .andExpectAll(
                        (status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE))),
                        (jsonPath("$.body[0].id").value(id)),
                        (jsonPath("$.body[0].hide").value(hide))
                );
    }

    @Test
    @DisplayName("test for hide monitor record without hide")
    void hideOrShowMonitorRecordTwoTest() throws Exception {
        String uri = PARENT_URL + HIDE_URI + RECORD_ID_URI;
        long id = 55L;
        boolean hide = false;   //Spring correctly interprets ?bar=0 or ?bar=no too
        mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("hide", String.valueOf(hide)))
                .andExpectAll(
                        (status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE))),
                        (jsonPath("$.body[0].id").value(id)),
                        (jsonPath("$.body[0].hide").value(hide))
                );
    }

    @Test
    @DisplayName("test for hide monitor record with invalid id")
    void hideOrShowMonitorRecordWithInvalidIdTest() throws Exception {
        String uri = PARENT_URL + HIDE_URI + RECORD_ID_URI;
        long id = -55L;
        mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE, INVALID_ID_MESSAGE)
                );
    }

    @Test
    @DisplayName("test for hide monitor record with wrong id")
    void hideOrShowMonitorRecordWithWrongIdTest() throws Exception {
        String uri = PARENT_URL + HIDE_URI + RECORD_ID_URI;
        long id = 550L;
        mockMvc.perform(MockMvcRequestBuilders.put(uri, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE, String.format(NOT_FOUND_RECORD_MESSAGE, id))
                );
    }

    @Test
    @DisplayName("test for get monitor record with id and filters")
    void getMonitorRecordFromTableTest() throws Exception {
        long requestId = 20L;
        String uri = PARENT_URL + TABLE_URI + RECORD_ID_URI;
        MultiValueMap<String, String> requestParams = getMapWithRequestParamsFromJson();
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId)
                        .params(requestParams))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body", hasSize(BODY_LIST_SIZE))),
                        (jsonPath("$.body[0].id").value(requestId)),
                        (jsonPath("$.body[0].servicename").value("service name")),
                        (jsonPath("$.body[0].serviceaddress").value("service address")),
                        (jsonPath("$.body[0].status").value("Error")));
    }

    @Test
    @DisplayName("test for get monitor record with invalid id")
    void getMonitorRecordFromTableByInvalidIdTest() throws Exception {
        long requestId = -9L;
        String uri = PARENT_URL + TABLE_URI + RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for get monitor record with wrong id")
    void getMonitorRecordFromTableByWrongIdTest() throws Exception {
        long requestId = 405L;
        String uri = PARENT_URL + TABLE_URI + RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, requestId)));
    }

    private MultiValueMap<String, String> getMapWithRequestParamsFromJson() throws IOException {
        ObjectMapper objectMapperWithParam = new ObjectMapper();
        objectMapperWithParam.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        String pathToJson = ResourceUtils.getFile(PATH_TO_JSON_FILE).getPath();
        String jsonAsString = new String(Files.readAllBytes(Paths.get(pathToJson)), StandardCharsets.UTF_8);
        Map<String, List<String>> readValues = objectMapperWithParam.readValue(jsonAsString, new TypeReference<Map<String, List<String>>>() {
        });
        return new LinkedMultiValueMap<>(readValues);
    }
}