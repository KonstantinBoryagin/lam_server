package ru.example.lam.server.service.transport;

import ru.example.lam.server.coreserver.transport.TransportDTO;

import java.util.List;

/**
 * Интерфейс для создания транспортного контракта
 *
 * @see TransportDTO
 */
public interface IBaseTransportService {

    /**
     * Формирует транспортный контракт с кодом 200 и листом полученных объектов
     *
     * @param objects объект или массив объектов для ответа
     * @param <T>     тип возвращаемого объекта
     * @return {@link TransportDTO} с успешным ответом
     */
    <T> TransportDTO<T> createSuccessfulTransportDTO(T... objects);

    /**
     * Формирует транспортный контракт с кодом 200, листом полученных объектов, и сообщением
     *
     * @param messages сообщение
     * @param objects  объект или массив объектов для ответа
     * @param <T>      тип возвращаемого объекта
     * @return {@link TransportDTO} с успешным ответом
     */
    <T> TransportDTO<T> createSuccessfulTransportDTOWithMessage(List<String> messages, T... objects);

    /**
     * Формирует транспортный контракт с листом полученных объектов
     *
     * @param object объект или массив объектов для ответа
     * @return {@link TransportDTO} с успешным ответом
     */
    <T> TransportDTO<T> createEmptySuccessfulTransportDTO(T object);

    /**
     * Формирует транспортный контракт с кодом 404 и листом полученных исключений
     *
     * @param object объект или массив объектов для ответа
     * @param exceptions исключения
     * @return {@link TransportDTO} с ошибкой
     */
    <T> TransportDTO<T> createErrorTransportDTO(T object, Exception... exceptions);

    /**
     * Формирует транспортный контракт с кодом, именем системы и листом полученных исключений
     *
     * @param object     тип для параметризации {@link TransportDTO}
     * @param systemName имя системы в которой произошла ошибка
     * @param exceptions исключения произошедшие в программе
     * @param errorCode  код ошибки
     * @return {@link TransportDTO} с ошибкой
     */
    <T> TransportDTO<T> createErrorTransportDTO(T object, String systemName, Integer errorCode, Exception... exceptions);

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
    <T> TransportDTO<T> createErrorTransportDTO(T object, String systemName, String title,
                                                Integer errorCode, Exception... exceptions);

    /**
     * Формирует пустой транспортный контракт с ошибкой
     *
     * @param object    тип для параметризации {@link TransportDTO}
     * @param errorCode код ошибки
     * @return {@link TransportDTO} с ошибкой
     */

    <T> TransportDTO<T> createEmptyErrorTransportDTO(T object, Integer errorCode);

    /**
     * Формирует транспортный контракт с кодом 404, листом полученных исключений и причиной ошибки.
     * Используется для исключений от Hibernate.
     *
     * @param object     объект или массив объектов для ответа
     * @param exceptions исключения
     * @param <T>        тип возвращаемого объекта
     * @return {@link TransportDTO} с причиной ошибки ошибкой
     */
    <T> TransportDTO<T> createErrorTransportDTOWithCause(T object, Exception... exceptions);

    /**
     * Формирует транспортный контракт с кодом и листом полученных исключений
     *
     * @param object     тип для параметризации {@link TransportDTO}
     * @param exceptions исключения произошедшие в программе
     * @param errorCode  код ошибки
     * @return {@link TransportDTO} с ошибкой
     */
    <T> TransportDTO<T> createErrorTransportDTO(T object, Integer errorCode, Exception... exceptions);
}
