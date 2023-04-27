package ru.example.lam.server.service.transport.implementation;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.example.lam.server.coreserver.transport.ErrorDTO;
import ru.example.lam.server.coreserver.transport.MessageDTO;
import ru.example.lam.server.coreserver.transport.TransportDTO;
import ru.example.lam.server.service.transport.IBaseTransportService;

/**
 * Реализация интерфейса для создания транспортного контракта {@link IBaseTransportService}
 */
@Service
@Log4j2
public class BaseTransportServiceImpl implements IBaseTransportService {
    public static final Integer SUCCESS_CODE = HttpStatus.OK.value();
    public static final Integer ERROR_NOT_FOUND_CODE = HttpStatus.NOT_FOUND.value();
    public static final Integer ERROR_BAD_REQUEST_CODE = HttpStatus.BAD_REQUEST.value();
    public static final String ERROR_TITLE = "error";
    public static final String MESSAGE_TITLE = "wrong role value";
    public static final String MESSAGE_WRONG_ROLE_MESSAGE = "This role are not found in the database";
    @Value("${AppSysName}")
    private String systemName;

    /**
     * Формирует транспортный контракт с кодом 200 и листом полученных объектов
     *
     * @param objects объект или массив объектов для ответа
     * @return {@link TransportDTO} с успешным ответом
     */
    @Override
    public <T> TransportDTO<T> createSuccessfulTransportDTO(T... objects) {
        log.debug("Method createSuccessfulTransportDTO with code: {} is done {}", SUCCESS_CODE, Arrays.toString(objects));
        return TransportDTO.<T>builder()
                .success(true)
                .code(SUCCESS_CODE)
                .body(Arrays.asList(objects))
                .build();
    }

    /**
     * Формирует транспортный контракт с кодом 200, листом полученных объектов, и сообщением
     *
     * @param messages сообщение
     * @param objects  объект или массив объектов для ответа
     * @param <T>      тип возвращаемого объекта
     * @return {@link TransportDTO} с успешным ответом
     */
    @Override
    public <T> TransportDTO<T> createSuccessfulTransportDTOWithMessage(List<String> messages, T... objects) {
        log.debug("Method createSuccessfulTransportDTOWithMessage {}, {}, {} and with code: {} is done {}", MESSAGE_TITLE, MESSAGE_WRONG_ROLE_MESSAGE, messages, SUCCESS_CODE , Arrays.toString(objects));
        return TransportDTO.<T>builder()
                .success(true)
                .code(SUCCESS_CODE)
                .body(Arrays.asList(objects))
                .message(formMessagesList(MESSAGE_TITLE, MESSAGE_WRONG_ROLE_MESSAGE, messages))
                .build();
    }

    /**
     * Формирует транспортный контракт с листом полученных объектов
     *
     * @param object объект или массив объектов для ответа
     * @return {@link TransportDTO} с успешным ответом
     */
    @Override
    public <T> TransportDTO<T> createEmptySuccessfulTransportDTO(T object) {
        log.debug("Method createEmptySuccessfulTransportDTO with code: {} is done {}", SUCCESS_CODE, object);
        return TransportDTO.<T>builder()
                .success(true)
                .code(SUCCESS_CODE)
                .build();
    }

    /**
     * Формирует транспортный контракт с кодом 404 и листом полученных исключений
     *
     * @param object объект или массив объектов для ответа
     * @param exceptions исключения
     * @return {@link TransportDTO} с ошибкой
     */
    @Override
    public <T> TransportDTO<T> createErrorTransportDTO(T object, Exception... exceptions) {
        log.debug("Method createErrorTransportDTO with code: {} is done {}; {}", ERROR_NOT_FOUND_CODE, object, Arrays.toString(exceptions));
        return TransportDTO.<T>builder()
                .success(false)
                .code(ERROR_NOT_FOUND_CODE)
                .errors(formErrorBody(ERROR_NOT_FOUND_CODE, exceptions))
                .build();
    }

    /**
     * Формирует транспортный контракт с кодом 404, листом полученных исключений и причиной ошибки.
     * Используется для исключений от Hibernate.
     *
     * @param object     объект или массив объектов для ответа
     * @param exceptions исключения
     * @param <T>        тип возвращаемого объекта
     * @return {@link TransportDTO} с причиной ошибки ошибкой
     */
    @Override
    public <T> TransportDTO<T> createErrorTransportDTOWithCause(T object, Exception... exceptions) {
        log.debug("Method createErrorTransportDTOWithCause with code: {} is done {}; {}", ERROR_BAD_REQUEST_CODE, object, Arrays.toString(exceptions));
        return TransportDTO.<T>builder()
                .success(false)
                .code(ERROR_BAD_REQUEST_CODE)
                .errors(formErrorBodyWithCause(exceptions))
                .build();
    }

    /**
     * Формирует транспортный контракт с кодом и листом полученных исключений
     *
     * @param object     тип для параметризации {@link TransportDTO}
     * @param exceptions исключения произошедшие в программе
     * @param errorCode  код ошибки
     * @return {@link TransportDTO} с ошибкой
     */
    @Override
    public <T> TransportDTO<T> createErrorTransportDTO(T object, Integer errorCode, Exception... exceptions) {
        log.debug("Method createErrorTransportDTO with code: {} is done {}; {}", errorCode, object, Arrays.toString(exceptions));
        return TransportDTO.<T>builder()
                .success(false)
                .code(errorCode)
                .errors(formErrorBody(errorCode, exceptions))
                .build();
    }

    /**
     * Формирует транспортный контракт с кодом, именем системы и листом полученных исключений
     *
     * @param object     тип для параметризации {@link TransportDTO}
     * @param systemName имя системы в которой произошла ошибка
     * @param exceptions исключения произошедшие в программе
     * @param errorCode  код ошибки
     * @return {@link TransportDTO} с ошибкой
     */
    @Override
    public <T> TransportDTO<T> createErrorTransportDTO(T object, String systemName, Integer errorCode, Exception... exceptions) {
        log.debug("Method createErrorTransportDTO is done with code: {} and systemName: {}; {}; {}", errorCode, systemName, object, Arrays.toString(exceptions));
        return TransportDTO.<T>builder()
                .success(false)
                .code(errorCode)
                .errors(formErrorBody(errorCode, systemName, exceptions))
                .build();
    }

    /**
     * Формирует транспортный контракт с кодом, именем системы, описанием события и листом полученных исключений
     *
     * @param object     тип для параметризации {@link TransportDTO}
     * @param systemName имя системы в которой произошла ошибка
     * @param title      описание события
     * @param exceptions исключения произошедшие в программе
     * @param errorCode  код ошибки
     * @return {@link TransportDTO} с ошибкой
     */
    @Override
    public <T> TransportDTO<T> createErrorTransportDTO(T object, String systemName, String title,
                                                       Integer errorCode, Exception... exceptions) {
        log.debug("Method createErrorTransportDTO with code: {}, systemName: {} and title {} is done {}; {}", errorCode , systemName, title, object, Arrays.toString(exceptions));
        return TransportDTO.<T>builder()
                .success(false)
                .code(errorCode)
                .errors(formErrorBody(errorCode, systemName, title, exceptions))
                .build();
    }

    /**
     * Формирует пустой транспортный контракт с ошибкой
     *
     * @param object    тип для параметризации {@link TransportDTO}
     * @param errorCode код ошибки
     * @return {@link TransportDTO} с ошибкой
     */
    @Override
    public <T> TransportDTO<T> createEmptyErrorTransportDTO(T object, Integer errorCode) {
        log.debug("Method createEmptyErrorTransportDTO with code: {} is done {}", errorCode, object);
        return TransportDTO.<T>builder()
                .success(false)
                .code(errorCode)
                .build();
    }

    /**
     * Формирует List из {@link ErrorDTO} для метода
     *
     * @param exceptions исключения
     * @return List исключений
     * @see #createErrorTransportDTO(Object, Exception...)
     * @see #createErrorTransportDTO(Object, Integer, Exception...)
     */
    private List<ErrorDTO<Exception>> formErrorBody(int errorCode, Exception... exceptions) {
        log.debug("Method formErrorBody is done with code: {}; {}", errorCode, Arrays.toString(exceptions));
        return formErrorList(errorCode, systemName, null, exceptions);
    }

    /**
     * Формирует List из {@link ErrorDTO} для метода
     *
     * @param errorCode  код ошибки
     * @param systemName имя системы в которой произошла ошибка
     * @param exceptions исключения произошедшие в программе
     * @return List исключений
     */
    private List<ErrorDTO<Exception>> formErrorBody(int errorCode, String systemName, Exception... exceptions) {
        log.debug("Method formErrorBody is done with code: {} and systemName {}; {}", errorCode, systemName, Arrays.toString(exceptions));
        return formErrorList(errorCode, systemName, null, exceptions);
    }

    /**
     * Формирует List из {@link ErrorDTO} для метода
     *
     * @param errorCode  код ошибки
     * @param systemName имя системы в которой произошла ошибка
     * @param title      описание события
     * @param exceptions исключения произошедшие в программе
     * @return List исключений
     */
    private List<ErrorDTO<Exception>> formErrorBody(int errorCode, String systemName, String title,
                                                    Exception... exceptions) {
        log.debug("Method formErrorBody is done with code: {}, systemName: {} and title {}; {}", errorCode, systemName, title, Arrays.toString(exceptions));
        return formErrorList(errorCode, systemName, title, exceptions);
    }

    /**
     * Формирует list {@link ErrorDTO} из полученных исключений
     *
     * @param exceptions исключения
     * @return List {@link ErrorDTO}
     */
    private List<ErrorDTO<Exception>> formErrorBodyWithCause(Exception... exceptions) {
        log.debug("Method formErrorBodyWithCause is done {}", Arrays.toString(exceptions));
        return formErrorListWithCause(exceptions);
    }


    /**
     * Формирует list {@link ErrorDTO}
     *
     * @param errorCode  код ошибки
     * @param systemName имя системы в которой произошла ошибка
     * @param title      заголовок
     * @param exceptions исключения
     * @return List {@link ErrorDTO}
     */
    private List<ErrorDTO<Exception>> formErrorList(int errorCode, String systemName, String title, Exception... exceptions) {
        log.debug("Method formErrorList is started");
        List<ErrorDTO<Exception>> errors = new ArrayList<>();
        for (Exception exception : exceptions) {
            ErrorDTO<Exception> error = ErrorDTO.builder()
                    .code(errorCode)
                    .system(systemName)
                    .title(title == null ? ERROR_TITLE : title)
                    .message(exception.getMessage())
                    .build();
            errors.add(error);
            log.debug("Errors added {}", errors);
        }
        return errors;
    }

    /**
     * Формирует list {@link ErrorDTO} на основе getCause() методов, используется для SQL исключений
     *
     * @param exceptions исключения
     * @return List {@link ErrorDTO}
     */
    private List<ErrorDTO<Exception>> formErrorListWithCause(Exception... exceptions) {
        log.debug("Method formErrorListWithCause is started");
        List<ErrorDTO<Exception>> errors = new ArrayList<>();
        for (Exception exception : exceptions) {
            ErrorDTO<Exception> error = ErrorDTO.builder()
                    .code(ERROR_BAD_REQUEST_CODE)
                    .system(systemName)
                    .title(ERROR_TITLE)
                    .message(exception.getCause().getCause().getLocalizedMessage().split("\n")[0])
                    .build();
            errors.add(error);
            log.debug("Errors added {}", errors);
        }
        return errors;
    }

    /**
     * Формирует list {@link MessageDTO}
     *
     * @param title       заголовок
     * @param messageText краткое сообщение
     * @param messages    основные сообщения
     * @return list {@link MessageDTO}
     */
    private List<MessageDTO<String>> formMessagesList(String title, String messageText, List<String> messages) {
        log.debug("Method formMessagesList is started");
        List<MessageDTO<String>> messagesDTOList = new ArrayList<>();
        for (String message : messages) {
            MessageDTO<String> messageDTO = MessageDTO.<String>builder()
                    .title(title)
                    .message(messageText)
                    .body(new ArrayList<>(Collections.singletonList(message)))
                    .build();
            messagesDTOList.add(messageDTO);
            log.debug("Messages list added {}", messagesDTOList);
        }
        return messagesDTOList;
    }
}
