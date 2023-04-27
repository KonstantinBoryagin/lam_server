package ru.example.lam.server.service.deserialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;
import ru.example.lam.server.dto.monitor.TimeLine;

/**
 * Тестовый класс для {@link CustomDateDeserializer}
 */

@BootTest
class CustomDateDeserializerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest(name = "{index} test for serialization and deserialization LocalDateTime values with Jackson")
    @MethodSource("getParametersForDeserializerTest")
    void deserializeTest(String value) throws JsonProcessingException {
        //сериализуем
        TimeLine timeLine = objectMapper.readValue(value, TimeLine.class);
        //десериализуем
        String actualResult = objectMapper.writeValueAsString(timeLine);
        //сравниваем
        assertEquals(value, actualResult);
    }

    @Test
    @DisplayName("test for exception in serialize")
    void serializeExceptionTest() {
        String jsonForTest = "{\"time\":\"12-12-2012 12:2:54.000\"}";
        JsonMappingException jsonMappingException = assertThrows(JsonMappingException.class,
                () -> objectMapper.readValue(jsonForTest, TimeLine.class), "No JsonMappingException");
        assertTrue(jsonMappingException.getMessage().contains("could not be parsed at"));
    }

    public static Stream<Arguments> getParametersForDeserializerTest() {
        return Stream.of(
                arguments("{\"time\":\"12.12.2012 12:12:54.000\",\"value\":\"value\"}"),
                arguments("{\"time\":\"13.12.2013 13:12:12.001\",\"value\":\"value\"}"),
                arguments("{\"time\":\"14.12.2014 14:18:59.168\",\"value\":\"value\"}"),
                arguments("{\"time\":\"15.12.2015 15:17:07.568\",\"value\":\"value\"}"),
                arguments("{\"time\":\"16.12.2016 16:13:25.710\",\"value\":\"value\"}"),
                arguments("{\"time\":\"17.12.2017 17:52:45.453\",\"value\":\"value\"}"),
                arguments("{\"time\":\"18.12.2018 18:19:08.001\",\"value\":\"value\"}"),
                arguments("{\"time\":\"19.12.2019 19:46:10.065\",\"value\":\"value\"}"),
                arguments("{\"time\":\"20.12.2020 20:14:25.007\",\"value\":\"value\"}"),
                arguments("{\"time\":\"12.12.2021 21:12:56.976\",\"value\":\"value\"}"),
                arguments("{\"time\":\"22.12.2022 22:53:35.615\",\"value\":\"value\"}"),
                arguments("{\"time\":\"23.12.2023 23:57:17.090\",\"value\":\"value\"}")
        );
    }
}