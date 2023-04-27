package ru.example.lam.server.service.journal.implementation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.journal.AddJournalDTO;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.journal.JournalTableDTO;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.model.journal.JournalTableRequest;
import ru.example.lam.server.service.journal.IJournalService;

/**
 * Реализация {@link IJournalService}
 * Задействуется в профиле 'dev' и 'prod'
 */
@Service
@Profile({"dev", "devs", "prod"})
public class JournalServiceImpl implements IJournalService {

    @Override
    public JournalTableDTO getJournalTable(JournalTableRequest journalTableRequest) {
        return null;
    }

    @Override
    public JournalRecordDTO getJournalRecordById(Long journalRecordId) {
        return null;
    }

    @Override
    public boolean deleteJournalRecordById(Long journalRecordId) {
        return false;
    }

    @Override
    public JournalRecordDTO addJournal(AddJournalDTO addJournalDTO) {
        return null;
    }

    @Override
    public JournalRecordDTO getJournalRecordByIdWithFilters(Long journalRecordId, JournalRecordRequestWithId journalRecordRequestWithId) {
        return null;
    }
}
