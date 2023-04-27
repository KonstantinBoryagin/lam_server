package ru.example.lam.server.service.journal;

import lombok.Getter;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

import ru.example.lam.server.dto.journal.AddJournalDTO;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.journal.JournalTableDTO;
import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.dto.journal.enums.JournalMessageType;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.model.journal.JournalTableRequest;
import ru.example.lam.server.service.mapping.DtoMapper;

/**
 * Временная "заглушка" для тестовой реализации {@link IJournalService}
 */
@Service
@Profile("test")
public class JournalServiceStabImpl implements IJournalService {

    @Autowired
    private DtoMapper dtoMapper;
    @Getter
    private JournalRecordDTO journalRecordDTOById;


    @Override
    public JournalTableDTO getJournalTable(JournalTableRequest journalTableRequest) {

    return dtoMapper.toJournalTableDto(journalTableRequest);
    }

    @Override
    public JournalRecordDTO getJournalRecordById(Long journalRecordId) throws LamServerException {
        if (journalRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        if (journalRecordId > 400) {
            throw new LamServerException("Record with this id does not exist");
        }
        journalRecordDTOById =  JournalRecordDTO.builder()
                .id(journalRecordId)
                .dateTime(LocalDateTime.of(2022, 5, 4, 0, 10, 23))
                .userId("123")
                .tagsDtoList(Arrays.asList(TagDTO.builder().name("tag_1").value("value_1").build(),
                        TagDTO.builder().name("tag_2").value("value_2").build()))
                .subSystemName("sub system name")
                .host("host name")
                .journalMessageType(JournalMessageType.DEBUG)
                .username("John")
                .header("header value")
                .message("message from service")
                .dataSource("date source")
                .method("get")
                .eventTime(LocalDateTime.of(2022, 2, 15, 10, 25, 32))
                .namespace("name spaces")
                .request("request from client")
                .response("response from server")
                .version("1.0")
                .build();
        return journalRecordDTOById;
    }

    @Override
    public boolean deleteJournalRecordById(Long journalRecordId) throws LamServerException {
        if (journalRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        return journalRecordId <= 400;
    }

    @Override
    public JournalRecordDTO addJournal(AddJournalDTO addJournalDTO) throws LamServerException {
        if (addJournalDTO == null) {
            throw new LamServerException("addJournalDTO is null");
        }
        return dtoMapper.fromAddToJournalRecordDto(addJournalDTO);
    }

    @Override
    public JournalRecordDTO getJournalRecordByIdWithFilters(Long journalRecordId, JournalRecordRequestWithId journalRecordRequestWithId) throws LamServerException {
        if (journalRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        if (journalRecordId > 400) {
            throw new LamServerException("Record with this id does not exist");
        }

        JournalRecordDTO journalRecordDTO = dtoMapper.toJournalDto(journalRecordRequestWithId);
        journalRecordDTO.setId(journalRecordId);
        return journalRecordDTO;
    }
}