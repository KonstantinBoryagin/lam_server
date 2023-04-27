package ru.example.lam.server.service.journal;

import ru.example.lam.server.controller.journal.JournalController;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.journal.AddJournalDTO;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.journal.JournalTableDTO;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.model.journal.JournalTableRequest;

/**
 * Интерфейс для взаимодействия {@link JournalController} с БД
 */
public interface IJournalService {

    /**
     * Получение таблицы журнала согласно входящим параметрам
     *
     * @param journalTableRequest POJO с параметрами запроса
     * @return DTO с таблицей журнала {@link JournalTableDTO}
     */
    JournalTableDTO getJournalTable(JournalTableRequest journalTableRequest);

    /**
     * Получение записи журнала по id
     *
     * @param journalRecordId id записи в журнале
     * @return DTO записи в журнале {@link JournalRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    JournalRecordDTO getJournalRecordById(Long journalRecordId) throws LamServerException;

    /**
     * Удаление записи журнала по id
     *
     * @param journalRecordId id записи в журнале
     * @return boolean значение успешности запроса на удаление
     * @throws LamServerException исключение с причиной провала операции
     */
    boolean deleteJournalRecordById(Long journalRecordId) throws LamServerException;

    /**
     * Добавление записи в журнал
     *
     * @param addJournalDTO DTO с записью журнала для сохранения
     * @return DTO записи в журнале {@link JournalRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    JournalRecordDTO addJournal(AddJournalDTO addJournalDTO) throws LamServerException;

    /**
     * Получение записи журнала согласно входящим параметрам
     *
     * @param journalRecordId            id записи в журнале
     * @param journalRecordRequestWithId POJO с параметрами запроса
     * @return DTO записи в журнале {@link JournalRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    JournalRecordDTO getJournalRecordByIdWithFilters(Long journalRecordId, JournalRecordRequestWithId journalRecordRequestWithId) throws LamServerException;

}
