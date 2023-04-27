package ru.example.lam.server.controller.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static ru.example.lam.server.controller.MockMvcTestUtil.getResultWithError;
import static ru.example.lam.server.service.feature.FeatureServiceStubImpl.EMPTY_REQUEST_BODY_MESSAGE;
import static ru.example.lam.server.service.feature.FeatureServiceStubImpl.INVALID_ID_MESSAGE;
import static ru.example.lam.server.service.feature.FeatureServiceStubImpl.NOT_FOUND_RECORD_MESSAGE;
import static ru.example.lam.server.service.feature.FeatureServiceStubImpl.NOT_FOUND_VERSION_RECORD_MESSAGE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_NOT_FOUND_CODE;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.ERROR_TITLE;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.configuration.annotation.MockMvcTest;
import ru.example.lam.server.controller.MockMvcTestUtil;
import ru.example.lam.server.dto.feature.CreateFeatureDTO;
import ru.example.lam.server.dto.feature.EditFeatureDTO;
import ru.example.lam.server.dto.feature.FeatureRecordDTO;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.service.feature.FeatureServiceStubImpl;
import ru.example.lam.server.service.mapping.DtoMapper;

/**
 * Тестовый класс для {@link FeatureController}
 */
@MockMvcTest
@BootTest
class FeatureControllerTest {
    private static final String PARENT_URL = "http://localhost:8080/api/feature";
    private static final String TABLE_URI = "/featuretable";
    private static final String RECORD_URI = "/{featurerecordid}";
    private static final String NAME_SYSTEM_VERSION_URI = "/{featurename}/{subsystemname}/{version}";
    private static final String CREATE_URI = "/createfeature";
    private static final String EDIT_URI = "/editfeature";
    @Value("${AppSysName}")
    private String systemName;
    private static final int LIST_SIZE = 1;
    private static final String JSON_PATH = "classpath:json/feature/featureRequestParams.json";
    @Autowired
    private FeatureServiceStubImpl featureServiceStub;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DtoMapper dtoMapper;
    @Autowired
    private MockMvc mockMvc;
    private static CreateFeatureDTO createFeatureDTO;

    @BeforeAll
    static void init() {
        createFeatureDTO = CreateFeatureDTO.builder()
                .featureName("bug fix feature")
                .featureStatus(false)
                .subsystem("LAM core system")
                .description("test feature record")
                .version("1.0.0 TEST")
                .comment("for test FeatureController")
                .build();
    }


    @Test
    @DisplayName("test for get feature table request")
    void getFeatureTableTest() throws Exception {
        String uri = PARENT_URL + TABLE_URI;
        mockMvc.perform(get(uri)
                        .params(getTestRequestParam()))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpectAll((jsonPath("$.body", hasSize(LIST_SIZE))),
                        (jsonPath("$.body[0].featurename").value("feature name value")),
                        (jsonPath("$.body[0].description").value("test feature request")),
                        (jsonPath("$.body[0].updatetime").value("15.02.2022 10:25:32.555")),
                        (jsonPath("$.body[0].version").value("5.0.2 RELEASE")),
                        (jsonPath("$.body[0].featurestatus").value("true"))
                );
    }

    @Test
    @DisplayName("test for get feature table request with wrong parameter")
    void getFeatureTableFailureTest() throws Exception {
        String uri = PARENT_URL + TABLE_URI;
        mockMvc.perform(get(uri)
                        .param("featurestatus", "t"))  //boolean
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE));
    }

    @Test
    @DisplayName("test for get feature record by id")
    void getFeatureRecordByIdTest() throws Exception {
        String uri = PARENT_URL + RECORD_URI;
        Long id = 345L;
        MvcResult mvcResult = mockMvc.perform(get(uri, id))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(LIST_SIZE)))
                .andReturn();
        //проверяем что вернулся правильный объект
        FeatureRecordDTO expectedObject = featureServiceStub.getTestFeatureRecord(id);
        List<FeatureRecordDTO> responseObjectsList = convertResponseToFeatureRecord(mvcResult);
        assertEquals(expectedObject, responseObjectsList.get(0));
    }

    @Test
    @DisplayName("test for get feature record by invalid id")
    void getFeatureRecordByInvalidIdTest() throws Exception {
        String uri = PARENT_URL + RECORD_URI;
        Long id = -345L;
        mockMvc.perform(get(uri, id))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for get feature record by wrong id")
    void getFeatureRecordByWrongIdTest() throws Exception {
        String uri = PARENT_URL + RECORD_URI;
        Long id = 2045L;
        mockMvc.perform(get(uri, id))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, id)));
    }

    @Test
    @DisplayName("test for delete feature record by id")
    void deleteFeatureRecordByIdTest() throws Exception {
        String uri = PARENT_URL + RECORD_URI;
        Long id = 156L;
        mockMvc.perform(delete(uri, id))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse());
    }

    @Test
    @DisplayName("test for delete feature record by invalid id")
    void deleteFeatureRecordByInvalidIdTest() throws Exception {
        String uri = PARENT_URL + RECORD_URI;
        Long id = -156L;
        mockMvc.perform(delete(uri, id))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for delete feature record by wrong id")
    void deleteFeatureRecordByWrongIdTest() throws Exception {
        String uri = PARENT_URL + RECORD_URI;
        Long id = 1506L;
        mockMvc.perform(delete(uri, id))
                .andExpectAll((status().isOk()),
                        (content().contentType(MediaType.APPLICATION_JSON)),
                        (jsonPath("$.success").value(false)),
                        (jsonPath("$.code").value(ERROR_NOT_FOUND_CODE)));
    }

    @Test
    @DisplayName("test for get feature record by featureName, subsystemName, version")
    void getFeatureRecordByNameAndSubSystemAndVersionTest() throws Exception {
        String uri = PARENT_URL + NAME_SYSTEM_VERSION_URI;
        String featureName = "bug fix";
        String subsystemName = "LAM CORE";
        String version = "1.0.2 RELEASE";
        MvcResult mvcResult = mockMvc.perform(get(uri, featureName, subsystemName, version))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(LIST_SIZE)))
                .andReturn();
        //проверяем что вернулся правильный объект
        FeatureRecordDTO expectedObject = featureServiceStub.getTestFeatureRecord(featureName, subsystemName, version);
        List<FeatureRecordDTO> responseObjectsList = convertResponseToFeatureRecord(mvcResult);
        assertEquals(expectedObject, responseObjectsList.get(0));
    }

    @Test
    @DisplayName("test for get feature record by featureName, subsystemName and wrong version")
    void getFeatureRecordByNameAndSubSystemAndVersionFailTest() throws Exception {
        String uri = PARENT_URL + NAME_SYSTEM_VERSION_URI;
        String featureName = "bug fix";
        String subsystemName = "LAM CORE";
        String version = "1.0";
        mockMvc.perform(get(uri, featureName, subsystemName, version))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_VERSION_RECORD_MESSAGE, version)));
    }

    @Test
    @DisplayName("test for create feature record")
    void createFeatureRecordTest() throws Exception {
        String uri = PARENT_URL + CREATE_URI;
        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.postWithJson(uri, createFeatureDTO))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(LIST_SIZE)))
                .andReturn();
        List<FeatureRecordDTO> responseObjectsList = convertResponseToFeatureRecord(mvcResult);
        FeatureRecordDTO expectedObject = dtoMapper.fromCreateToFeatureRecordDto(createFeatureDTO);
        assertEquals(expectedObject, responseObjectsList.get(0));
    }

    @Test
    @DisplayName("test for create feature record with empty body")
    void createFeatureRecordWithEmptyBodyTest() throws Exception {
        String uri = PARENT_URL + CREATE_URI;
        mockMvc.perform(MockMvcTestUtil.postWithJson(uri, null))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for edit feature record")
    void editFeatureRecordTest() throws Exception {
        String uri = PARENT_URL + EDIT_URI;
        Long id = 268L;
        EditFeatureDTO editFeatureObject = getTestEditFeatureObject(id);
        MvcResult mvcResult = mockMvc.perform(MockMvcTestUtil.putWithJson(uri, editFeatureObject))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpect(jsonPath("$.body", hasSize(LIST_SIZE)))
                .andReturn();
        //проверяем что вернулся правильный объект
        List<FeatureRecordDTO> responseObjectsList = convertResponseToFeatureRecord(mvcResult);
        FeatureRecordDTO expectedObject = dtoMapper.fromEditToFeatureRecordDto(editFeatureObject);
        assertEquals(expectedObject, responseObjectsList.get(0));
    }

    @Test
    @DisplayName("test for edit feature record with invalid id")
    void editFeatureRecordWithInvalidIdTest() throws Exception {
        String uri = PARENT_URL + EDIT_URI;
        Long id = -268L;
        EditFeatureDTO editFeatureObject = getTestEditFeatureObject(id);
        mockMvc.perform(MockMvcTestUtil.putWithJson(uri, editFeatureObject))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for edit feature record with wrong id")
    void editFeatureRecordWithWrongIdTest() throws Exception {
        String uri = PARENT_URL + EDIT_URI;
        Long id = 2628L;
        EditFeatureDTO editFeatureObject = getTestEditFeatureObject(id);
        mockMvc.perform(MockMvcTestUtil.putWithJson(uri, editFeatureObject))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, id)));
    }

    @Test
    @DisplayName("test for edit feature record with empty body")
    void editFeatureRecordWithEmptyBodyTest() throws Exception {
        String uri = PARENT_URL + EDIT_URI;
        mockMvc.perform(MockMvcTestUtil.putWithJson(uri, null))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        EMPTY_REQUEST_BODY_MESSAGE));
    }

    @Test
    @DisplayName("test for get feature record from table")
    void getFeatureRecordFromTableTest() throws Exception {
        String uri = PARENT_URL + TABLE_URI + RECORD_URI;
        Long id = 368L;
        mockMvc.perform(get(uri, id)
                        .params(getTestRequestParam()))
                .andExpectAll(MockMvcTestUtil.getResultWithSuccessResponse())
                .andExpectAll((jsonPath("$.body", hasSize(LIST_SIZE))),
                        (jsonPath("$.body[0].id").value(id)),
                        (jsonPath("$.body[0].featurename").value("feature name value")),
                        (jsonPath("$.body[0].description").value("test feature request")),
                        (jsonPath("$.body[0].updatetime").value("15.02.2022 10:25:32.555")),
                        (jsonPath("$.body[0].version").value("5.0.2 RELEASE")),
                        (jsonPath("$.body[0].pageoffset").value(24L)),
                        (jsonPath("$.body[0].featurestatus").value("true"))
                );
    }

    @Test
    @DisplayName("test for get feature record from table with invalid id")
    void getFeatureRecordFromTableWithInvalidIdTest() throws Exception {
        String uri = PARENT_URL + TABLE_URI + RECORD_URI;
        Long id = -368L;
        mockMvc.perform(get(uri, id)
                        .params(getTestRequestParam()))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        INVALID_ID_MESSAGE));
    }

    @Test
    @DisplayName("test for get feature record from table with wrong id")
    void getFeatureRecordFromTableWithWrongIdTest() throws Exception {
        String uri = PARENT_URL + TABLE_URI + RECORD_URI;
        Long id = 36328L;
        mockMvc.perform(get(uri, id)
                        .params(getTestRequestParam()))
                .andExpectAll(MockMvcTestUtil.getResultWithError(ERROR_NOT_FOUND_CODE, systemName, ERROR_TITLE,
                        String.format(NOT_FOUND_RECORD_MESSAGE, id)));
    }

    /**
     * Преобразует json файл в MultiValueMap с параметрами запроса
     */
    private MultiValueMap<String, String> getTestRequestParam() throws IOException {
        ObjectMapper objectMapperWithParam = new ObjectMapper();
        objectMapperWithParam.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        String pathToJson = ResourceUtils.getFile(JSON_PATH).getPath();
        String jsonAsString = new String(Files.readAllBytes(Paths.get(pathToJson)), StandardCharsets.UTF_8);
        Map<String, List<String>> readValues = objectMapperWithParam.readValue(jsonAsString, new TypeReference<Map<String, List<String>>>() {
        });
        return new LinkedMultiValueMap<>(readValues);
    }

    /**
     * Подготавливает и отдает проинициализированный объект {@link EditFeatureDTO}
     */
    private EditFeatureDTO getTestEditFeatureObject(Long id) {
        return EditFeatureDTO.builder()
                .id(id)
                .featureName("bug fix")
                .featureStatus(true)
                .subsystem("LAM core")
                .description("test feature")
                .version("1.2.0")
                .comment("for test FeatureController")
                .build();
    }

    /**
     * Конвертирует MvcResult в List<FeatureRecordDTO>
     */
    private List<FeatureRecordDTO> convertResponseToFeatureRecord(MvcResult mvcResult) throws UnsupportedEncodingException, JsonProcessingException {
        String responseAsString = mvcResult.getResponse().getContentAsString();
        TransportDTO<?> transportDTO = objectMapper.readValue(responseAsString, TransportDTO.class);
        return objectMapper.convertValue(transportDTO.getBody(), new TypeReference<List<FeatureRecordDTO>>() {
        });
    }

}