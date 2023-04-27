package ru.example.lam.server.service.monitor;

import ru.example.lam.server.controller.monitor.MonitorController;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.monitor.AddMonitorDTO;
import ru.example.lam.server.dto.monitor.MonitorChartDTO;
import ru.example.lam.server.dto.monitor.MonitorRecordDTO;
import ru.example.lam.server.dto.monitor.MonitorTableDTO;
import ru.example.lam.server.model.monitor.MonitorRequest;

/**
 * Интерфейс для взаимодействия {@link MonitorController} с БД
 */
public interface IMonitorService {

    /**
     * Получение таблицы мониторинга событий согласно входящим параметрам
     *
     * @param monitorRequest POJO с параметрами запроса
     * @return DTO таблицы мониторинга событий {@link MonitorTableDTO}
     */
    MonitorTableDTO getMonitorTable(MonitorRequest monitorRequest);

    /**
     * Получение записи мониторинга событий по id
     *
     * @param monitorRecordId id записи
     * @return DTO с записью мониторинга {@link MonitorRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    MonitorRecordDTO getMonitorRecordById(Long monitorRecordId) throws LamServerException;

    /**
     * Удаление записи мониторинга событий по id
     *
     * @param monitorRecordId id записи
     * @return boolean значение успешности запроса
     * @throws LamServerException исключение с причиной провала операции
     */
    boolean deleteMonitorRecordById(Long monitorRecordId) throws LamServerException;

    /**
     * Получение записи мониторинга событий по имени и версии сервиса
     *
     * @param serviceName имя сервиса
     * @param version     версия сервиса
     * @return DTO с записью мониторинга {@link MonitorRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    MonitorRecordDTO getMonitorRecordByServiceNameAndVersion(String serviceName,
                                                             String version) throws LamServerException;

    /**
     * Добавление записи в мониторинг
     *
     * @param addMonitorDTO DTO с записью для добавления в БД
     * @return DTO с записью мониторинга {@link MonitorRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    MonitorTableDTO addMonitorRecord(AddMonitorDTO addMonitorDTO) throws LamServerException;

    /**
     * Обновление записи в мониторинге событий по id
     *
     * @param monitorRecordId id записи в мониторинге
     * @param monitorChartDTO DTO с графиками для записи
     * @return DTO с записью мониторинга {@link MonitorRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    MonitorRecordDTO putRecordToMonitorChart(Long monitorRecordId, MonitorChartDTO monitorChartDTO) throws LamServerException;

    /**
     * Показать/скрыть запись в мониторинге по id
     *
     * @param monitorRecordId id записи в мониторинге
     * @param hide            boolean значение отвечающее за показ/скрытие записи
     * @return DTO с записью мониторинга {@link MonitorRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    MonitorRecordDTO hideOrShowMonitorRecord(Long monitorRecordId,
                                             boolean hide) throws LamServerException;

    /**
     * Получение записи мониторинга событий согласно входящим параметрам
     *
     * @param monitorRecordId id записи в мониторинге
     * @param monitorRequest  POJO с параметрами запроса
     * @return DTO с записью мониторинга {@link MonitorRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    MonitorRecordDTO getMonitorRecordFromTable(Long monitorRecordId,
                                               MonitorRequest monitorRequest) throws LamServerException;
}
