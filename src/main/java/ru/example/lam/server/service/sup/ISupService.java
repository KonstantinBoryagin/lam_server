package ru.example.lam.server.service.sup;

import ru.example.lam.server.controller.sup.SupController;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.sup.CreateSupDTO;
import ru.example.lam.server.dto.sup.EditSupDTO;
import ru.example.lam.server.dto.sup.SupRecordDTO;
import ru.example.lam.server.dto.sup.SupTableDTO;
import ru.example.lam.server.model.sup.SupObjectRequest;

/**
 * Интерфейс для взаимодействия {@link SupController} с БД
 */
public interface ISupService {

    /**
     * Получение таблицы СУП согласно входящим параметрам
     *
     * @param supObjectRequest POJO с параметрами запроса
     * @return DTO с таблицей СУП {@link SupTableDTO}
     */
    SupTableDTO getSupTable(SupObjectRequest supObjectRequest);

    /**
     * Получение записи СУП по id
     *
     * @param supRecordId id записи СУП
     * @return DTO записи СУП {@link SupRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    SupRecordDTO getSupRecordByID(Long supRecordId) throws LamServerException;

    /**
     * Удаление записи СУП событий по id
     *
     * @param supRecordId id записи СУП
     * @return boolean значение успешности запроса на удаление
     * @throws LamServerException исключение с причиной провала операции
     */
    boolean deleteSupRecordById(Long supRecordId) throws LamServerException;

    /**
     * Получение записи СУП по полному имени
     *
     * @param fullPath полное имя записи в СУП
     * @return DTO записи СУП {@link SupRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    SupRecordDTO getSupRecordByFullPath(String fullPath) throws LamServerException;

    /**
     * Сохраняет запись СУП в БД
     *
     * @param createSupDTO DTO с записью СУП для сохранения
     * @return DTO записи СУП {@link SupRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    SupRecordDTO createSupRecord(CreateSupDTO createSupDTO) throws LamServerException;

    /**
     * Изменение записи СУП
     *
     * @param editSupDTO DTO с изменениями для целевой записи СУП
     * @return DTO записи СУП {@link SupRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    SupRecordDTO editSupRecord(EditSupDTO editSupDTO) throws LamServerException;

    /**
     * Получение записи СУП согласно входящим параметрам
     *
     * @param supRecordId      id записи СУП
     * @param supObjectRequest POJO с параметрами запроса
     * @return DTO записи СУП {@link SupRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    SupRecordDTO getSupRecordByIdWithSort(Long supRecordId, SupObjectRequest supObjectRequest) throws LamServerException;
}
