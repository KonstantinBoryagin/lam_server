package ru.example.lam.server.service.audit.implementation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.audit.AddAuditDTO;
import ru.example.lam.server.dto.audit.AuditRecordDTO;
import ru.example.lam.server.dto.audit.AuditTableDTO;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;
import ru.example.lam.server.service.audit.IAuditService;

/**
 * Реализация {@link IAuditService}
 * Задействуется в профиле 'dev' и 'prod'
 */
@Service
@Profile({"dev", "devs", "prod"})
public class AuditServiceImpl implements IAuditService {
    @Override
    public AuditTableDTO getAuditTable(AuditTableRequest auditTableRequest) {
        return null;
    }

    @Override
    public AuditRecordDTO getAuditRecordById(Long auditRecordId) {
        return null;
    }

    @Override
    public boolean deleteAuditRecordById(Long auditRecordId) {
        return false;
    }

    @Override
    public AuditRecordDTO addAudit(AddAuditDTO addAuditDTO) {
        return null;
    }

    @Override
    public AuditRecordDTO getAuditRecordByIdWithFilters(Long auditRecordId, AuditRecordRequestWithId auditRecordRequestWithId) {
        return null;
    }
}
