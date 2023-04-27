package ru.example.lam.server.service.audit;

import ru.example.lam.server.controller.audit.AuditController;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.audit.AddAuditDTO;
import ru.example.lam.server.dto.audit.AuditRecordDTO;
import ru.example.lam.server.dto.audit.AuditTableDTO;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;

/**
 * Интерфейс для взаимодействия {@link AuditController} с БД
 */
public interface IAuditService {

    /**
     * Получение таблицы аудита согласно входящим параметрам
     *
     * @param auditTableRequest POJO с параметрами запроса
     * @return DTO с таблицей аудита {@link AuditTableDTO}
     */
    AuditTableDTO getAuditTable(AuditTableRequest auditTableRequest);

    /**
     * Получение записи аудита по id
     *
     * @param auditRecordId id записи аудита
     * @return DTO с записью аудита {@link AuditTableDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    AuditRecordDTO getAuditRecordById(Long auditRecordId) throws LamServerException;

    /**
     * Удаление записи аудита по id
     *
     * @param auditRecordId id записи аудита
     * @return boolean значение успешности запроса на удаление
     * @throws LamServerException исключение с причиной провала операции
     */
    boolean deleteAuditRecordById(Long auditRecordId) throws LamServerException;

    /**
     * Добавление записи аудита в БД
     *
     * @param addAuditDTO запись аудита для сохранения
     * @return DTO с записью аудита {@link AuditTableDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    AuditRecordDTO addAudit(AddAuditDTO addAuditDTO) throws LamServerException;

    /**
     * Получение записи аудита согласно входящим параметрам
     *
     * @param auditRecordId            id записи аудита
     * @param auditRecordRequestWithId POJO с параметрами запроса
     * @return DTO с записью аудита {@link AuditTableDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    AuditRecordDTO getAuditRecordByIdWithFilters(Long auditRecordId, AuditRecordRequestWithId auditRecordRequestWithId) throws LamServerException;

}
