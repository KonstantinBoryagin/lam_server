package ru.example.lam.server.controller.sup;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static ru.example.lam.server.service.sup.SupServiceStabImpl.EMPTY_REQUEST_BODY_MESSAGE;
import static ru.example.lam.server.service.sup.SupServiceStabImpl.NOT_FOUND_RECORD_MESSAGE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_NOT_FOUND_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_TITLE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.SUCCESS_CODE;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.configuration.annotation.MockMvcTest;
import ru.example.lam.server.controller.MockMvcTestUtil;
import ru.example.lam.server.dto.sup.CreateSupDTO;
import ru.example.lam.server.dto.sup.EditSupDTO;
import ru.example.lam.server.dto.sup.SupRecordDTO;
import ru.example.lam.server.dto.sup.enums.SupParameterType;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.service.mapping.DtoMapper;
import ru.example.lam.server.service.sup.SupServiceStabImpl;

/**
 * Тестовый класс для {@link SupController}
 */
@MockMvcTest
@BootTest
class SupControllerTest {
    private static final String PARENT_URL = "http://localhost:8080/api/sup";
    private static final String CREATE_SUP_URI = "/createsup";
    private static final String SUP_TABLE_URI = "/suptable";
    private static final String SUP_RECORD_ID_URI = "/{suprecordid}";
    private static final String SUP_FULL_PATH_URI = "/fullpath/{fullpath}";
    private static final String SUP_EDIT_URI = "/editsup";
    private static final String ACTUAL_ERROR_MESSAGE = "The 'id' is less than 1";
    private static final String WRONG_ID_ERROR_MESSAGE = "Record with this id does not exist";
    private static final String WRONG_FULL_PATH_ERROR_MESSAGE = "Parameter fullPath is invalid";
    private static final String PATH_TO_JSON_FILE = "classpath:json/sup/supRecordWithIdRequest.json";
    private static final int EXPECTED_LIST_SIZE = 1;
    @Autowired
    private ObjectMapper objectMapper;
    private static CreateSupDTO createSupDTO;
    private static EditSupDTO editSupDTO;
    private static EditSupDTO editSupDTOWithInvalidId;
    @Value("${AppSysName}")
    private String systemName;

    @Autowired
    private SupServiceStabImpl supServiceStab;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DtoMapper dtoMapper;

    @BeforeAll
    static void init() {
        createSupDTO = CreateSupDTO.builder()
                .parameter("Parameter value")
                .parameterName("name of param")
                .subSystem("subSystem")
                .supParameterType(SupParameterType.JSON)
                .version("100")
                .username("user123")
                .parameterValueList(Arrays.asList("asd", "213"))
                .comment("object for test")
                .build();
        editSupDTO = EditSupDTO.builder()
                .id(209L)
                .parameterName("new param")
                .parameterValueList(Arrays.asList("1 new", "2 new"))
                .comment("edit record")
                .build();
        editSupDTOWithInvalidId = EditSupDTO.builder()
                .id(300L)
                .parameterName("new param")
                .parameterValueList(Arrays.asList("1 new", "2 new"))
                .comment("edit record")
                .build();
    }

    @Test
    @DisplayName("test for get sup table with correct request")
    void getSupTableTest() throws Exception {
        String uri = PARENT_URL + SUP_TABLE_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .param("parameter", "param")
                        .param("parametername", "param name")
                        .param("subsystemname", "subsystem name")
                        .param("fullpath", "full path value")
                        .param("username", "user")
                        .param("parametertype", "JSON")
                        .param("offset", "offset")
                        .param("limit", "limit")
                        .param("sortby", "param name=desc")
                )
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)),
                        (jsonPath("$.body[0].fullpath").value("full path value")),
                        (jsonPath("$.body[0].parametername").value("param name")),
                        (jsonPath("$.body[0].parametertype").value("Json"))
                );
    }

    @Test
    @DisplayName("test for get sup table with invalid request")
    void getSupTableFailTest() throws Exception {
        String uri = PARENT_URL + SUP_TABLE_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .param("parametertype", "")) //empty param in enum
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE));
    }

    @Test
    @DisplayName("test for get sup record with invalid id")
    void getSupRecordByIdFailTest() throws Exception {
        int requestId = -20;
        String uri = PARENT_URL + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get sup record with wrong id")
    void getSupRecordByWrongIdTest() throws Exception {
        int requestId = 500;
        String uri = PARENT_URL + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, WRONG_ID_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get sup record with correct id")
    void getSupRecordByIdTest() throws Exception {
        int requestId = 200;
        String uri = PARENT_URL + SUP_RECORD_ID_URI;
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
        List<SupRecordDTO> auditRecordDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<SupRecordDTO>>() {
        });
        assertEquals(EXPECTED_LIST_SIZE, auditRecordDTOList.size());
        assertEquals(auditRecordDTOList.get(0), supServiceStab.getTestSupRecordDTO());
    }

    @Test
    @DisplayName("test for delete sup record with invalid id")
    void deleteSupRecordByInvalidIdTest() throws Exception {
        int requestId = -2;
        String uri = PARENT_URL + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for delete sup record with wrong id")
    void deleteSupRecordByWrongIdTest() throws Exception {
        int requestId = 401;
        String uri = PARENT_URL + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(false)),
                        (jsonPath("$.code").value(ERROR_NOT_FOUND_CODE)));
    }

    @Test
    @DisplayName("test for delete sup record with correct id")
    void deleteSupRecordByIdTest() throws Exception {
        int requestId = 20;
        String uri = PARENT_URL + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.delete(uri, requestId))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)));
    }

    @Test
    @DisplayName("test for get sup record with wrong id")
    void getSupRecordByWrongFullPathTest() throws Exception {
        String uri = PARENT_URL + SUP_FULL_PATH_URI;
        String fullPathValue = "as";
        mockMvc.perform(MockMvcRequestBuilders.get(uri, fullPathValue))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        WRONG_FULL_PATH_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get sup record with correct id")
    void getSupRecordByFullPathTest() throws Exception {
        String uri = PARENT_URL + SUP_FULL_PATH_URI;
        String fullPathValue = "app.subsys1.parmgroup1.param";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri, fullPathValue))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE)))
                .andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<SupRecordDTO> auditRecordDTOList = objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<SupRecordDTO>>() {
        });
        assertEquals(EXPECTED_LIST_SIZE, auditRecordDTOList.size());
        assertEquals(auditRecordDTOList.get(0), supServiceStab.getTestSupRecordDTO());
    }

    @Test
    @DisplayName("test for create sup record")
    void createSupRecordTest() throws Exception {
        String uri = PARENT_URL + CREATE_SUP_URI;
        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.postWithJson(uri, createSupDTO))
                .andExpectAll(
                        (status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE))
                ).andReturn();
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<SupRecordDTO> supRecordDTOList = objectMapper.convertValue(transportDTO.getBody(),
                new TypeReference<List<SupRecordDTO>>() {
                });
        SupRecordDTO expectedSupRecord = dtoMapper.fromCreateToSupRecordDto(createSupDTO);
        assertEquals(EXPECTED_LIST_SIZE, supRecordDTOList.size());
        assertEquals(expectedSupRecord, supRecordDTOList.get(0));

    }

    @Test
    @DisplayName("test for create sup record with empty requestBody")
    void createSupRecordEmptyRequestBodyTest() throws Exception {
        String uri = PARENT_URL + CREATE_SUP_URI;
        mockMvc.perform(MockMvcTestUtil.postWithJson(uri, null))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE, EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for edit sup record")
    void editSupRecordTest() throws Exception {
        String uri = PARENT_URL + SUP_EDIT_URI;
        String requestAsString = objectMapper.writeValueAsString(editSupDTO);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpectAll(
                        (status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(true)),
                        (jsonPath("$.code").value(SUCCESS_CODE))
                ).andReturn();
        //проверяем что получен правильный объект
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        List<SupRecordDTO> supRecordDTOList = objectMapper.convertValue(transportDTO.getBody(),
                new TypeReference<List<SupRecordDTO>>() {
                });


        assertEquals(EXPECTED_LIST_SIZE, supRecordDTOList.size());
        assertEquals(supServiceStab.getSupRecordDTO(), supRecordDTOList.get(0));
    }

    @Test
    @DisplayName("test for edit sup record with invalid id")
    void editSupRecordWithInvalidIdTest() throws Exception {
        String uri = PARENT_URL + SUP_EDIT_URI;
        String requestAsString = objectMapper.writeValueAsString(editSupDTOWithInvalidId);
        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, editSupDTOWithInvalidId.getId())));
    }

    @Test
    @DisplayName("test for edit sup record with empty request body")
    void editSupRecordWithEmptyRequestBodyTest() throws Exception {
        String uri = PARENT_URL + SUP_EDIT_URI;
        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE,
                        systemName, ERROR_TITLE, EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for get sup record with id and position/filters")
    void getSupRecordByIdWithFiltersTest() throws Exception {
        long requestId = 20L;
        String uri = PARENT_URL + SUP_TABLE_URI + SUP_RECORD_ID_URI;
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
                        (jsonPath("$.body", hasSize(1))),
                        (jsonPath("$.body[0].id").value(requestId)),
                        (jsonPath("$.body[0].parameter").value("parameter value")),
                        (jsonPath("$.body[0].parametername").value("parameter name")),
                        (jsonPath("$.body[0].fullpath").value("full path value")),
                        (jsonPath("$.body[0].username").value("admin")),
                        (jsonPath("$.body[0].pageoffset").value(27L)),
                        (jsonPath("$.body[0].parametertype").value("Array"))
                );
    }

    @Test
    @DisplayName("test for get sup record with invalid id")
    void getSupRecordByInvalidIdWithFiltersTest() throws Exception {
        int requestId = -9;
        String uri = PARENT_URL + SUP_TABLE_URI + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, ACTUAL_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("test for get sup record with wrong id")
    void getSupRecordByWrongIdWithFiltersTest() throws Exception {
        int requestId = 405;
        String uri = PARENT_URL + SUP_TABLE_URI + SUP_RECORD_ID_URI;
        mockMvc.perform(MockMvcRequestBuilders.get(uri, requestId))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE, WRONG_ID_ERROR_MESSAGE));
    }

}