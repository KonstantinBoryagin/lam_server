package ru.example.lam.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.example.lam.server.service.transport.implementation.BaseTransportServiceImpl.SUCCESS_CODE;

import ru.example.lam.server.coreserver.transport.ErrorDTO;

/**
 * Класс для формирования повторяющихся фрагментов кода в тестах с {@link MockMvc}
 */
public class MockMvcTestUtil {

    /**
     * Формирует {@link RequestBuilder} для post метода {@link MockMvc} теста
     *
     * @param uri  uri адрес post запроса
     * @param body объект передаваемый в post запросе
     * @return {@link RequestBuilder} с входящими параметрами
     */
    public static RequestBuilder postWithJson(String uri, Object body) {
        try {
            String json = new ObjectMapper().writeValueAsString(body);
            return MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Формирует {@link RequestBuilder} для post метода {@link MockMvc} теста
     *
     * @param uri  uri адрес post запроса
     * @param body объект передаваемый в post запросе
     * @return {@link RequestBuilder} с входящими параметрами
     */
    public static RequestBuilder putWithJson(String uri, Object body) {
        try {
            String json = new ObjectMapper().writeValueAsString(body);
            return MockMvcRequestBuilders.put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Формирует {@link ResultMatcher[]} со стандартной проверкой заголовков успешного ответа
     *
     * @return {@link ResultMatcher[]} со стандартной проверкой успешного ответа параметрами
     */
    public static ResultMatcher[] getResultWithSuccessResponse() {
        return new ResultMatcher[]{
                (status().isOk()),
                (content().contentType(MediaType.APPLICATION_JSON)),
                (jsonPath("$.success").value(true)),
                (jsonPath("$.code").value(SUCCESS_CODE))
        };
    }

    /**
     * Формирует {@link ResultMatcher[]} для проверки ответа {@link MockMvc} теста с {@link ErrorDTO}
     *
     * @param code       код ошибки
     * @param systemName имя системы в которой произошла ошибка
     * @param title      описание проблемы
     * @return {@link ResultMatcher[]} с полученными параметрами
     */
    public static ResultMatcher[] getResultWithError(int code, String systemName, String title) {
        return new ResultMatcher[]{
                (status().isOk()),
                (content().contentType(MediaType.APPLICATION_JSON)),
                (jsonPath("$.success").value(false)),
                (jsonPath("$.code").value(code)),
                (jsonPath("$.errors[0].code").value(code)),
                (jsonPath("$.errors[0].system").value(systemName)),
                (jsonPath("$.errors[0].title").value(title)),
        };
    }

    /**
     * Формирует {@link ResultMatcher[]} для проверки ответа {@link MockMvc} теста с {@link ErrorDTO}
     *
     * @param code       код ошибки
     * @param systemName имя системы в которой произошла ошибка
     * @param title      описание проблемы
     * @param message    сообщение с ошибкой
     * @return {@link ResultMatcher[]} с полученными параметрами
     */
    public static ResultMatcher[] getResultWithError(int code, String systemName, String title, String message) {
        return new ResultMatcher[]{
                (status().isOk()),
                (content().contentType(MediaType.APPLICATION_JSON)),
                (jsonPath("$.success").value(false)),
                (jsonPath("$.code").value(code)),
                (jsonPath("$.errors[0].code").value(code)),
                (jsonPath("$.errors[0].system").value(systemName)),
                (jsonPath("$.errors[0].title").value(title)),
                (jsonPath("$.errors[0].message").value(message)),
        };
    }
}

