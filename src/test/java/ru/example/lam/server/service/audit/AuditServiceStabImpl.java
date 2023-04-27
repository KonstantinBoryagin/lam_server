package ru.example.lam.server.service.audit;
import lombok.Getter;
import ru.example.lam.server.dto.audit.AddAuditDTO;
import ru.example.lam.server.dto.audit.AuditRecordDTO;
import ru.example.lam.server.dto.audit.AuditTableDTO;
import ru.example.lam.server.dto.audit.enums.AuditMessageType;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;
import ru.example.lam.server.service.journal.IJournalService;
import ru.example.lam.server.service.mapping.DtoMapper;

/**
 * Временная "заглушка" для тестовой реализации {@link IJournalService}
 */
@Service
@Profile("test")
public class AuditServiceStabImpl implements IAuditService{

    @Autowired
    private DtoMapper dtoMapper;
    @Getter
    private AuditRecordDTO auditRecordDTO;


    @Override
    public AuditTableDTO getAuditTable(AuditTableRequest auditTableRequest) {

        return dtoMapper.auditTableRequestToDto(auditTableRequest);
    }

    @Override
    public AuditRecordDTO getAuditRecordById(Long auditRecordId) throws LamServerException {
        if (auditRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        if (auditRecordId > 400) {
            throw new LamServerException("Record with this id does not exist");
        }
        auditRecordDTO =  AuditRecordDTO.builder()
                .id(auditRecordId)
                .dateTime(LocalDateTime.of(2020, 5, 15, 5, 55, 42))
                .userId("321")
                .tagsDtoList(Arrays.asList(TagDTO.builder().name("tag_11").value("value_11").build(),
                        TagDTO.builder().name("tag_22").value("value_22").build()))
                .subSystemName("sub system name audit")
                .host("host name audit")
                .auditMessageType(AuditMessageType.CREATE)
                .username("Peter")
                .header("header value test")
                .message("message from service test")
                .dataSource("date source test")
                .method("get")
                .eventTime(LocalDateTime.of(2022, 2, 15, 10, 25, 32))
                .namespace("name spaces")
                .request("request from client")
                .response("response from server")
                .version("1.0.0")
                .build();
        return auditRecordDTO;
    }

    @Override
    public boolean deleteAuditRecordById(Long auditRecordId) throws LamServerException {
        if (auditRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        return auditRecordId <= 400;
    }

    @Override
    public AuditRecordDTO addAudit(AddAuditDTO addAuditDTO) throws LamServerException {
        if (addAuditDTO == null) {
            throw new LamServerException("addAuditDTO is null");
        }
        return dtoMapper.fromAddToAuditRecordDto(addAuditDTO);
    }

    @Override
    public AuditRecordDTO getAuditRecordByIdWithFilters(Long auditRecordId, AuditRecordRequestWithId auditRecordRequestWithId) throws LamServerException {
        if (auditRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        if (auditRecordId > 400) {
            throw new LamServerException("Record with this id does not exist");
        }

        AuditRecordDTO auditRecordDTO = dtoMapper.toAuditRecordDto(auditRecordRequestWithId);
        auditRecordDTO.setId(auditRecordId);
        return auditRecordDTO;
    }
}
