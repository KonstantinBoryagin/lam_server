package ru.example.lam.server.service.mapping;

import org.mapstruct.Mapper;

import ru.example.lam.server.dto.audit.AddAuditDTO;
import ru.example.lam.server.dto.audit.AuditRecordDTO;
import ru.example.lam.server.dto.audit.AuditTableDTO;
import ru.example.lam.server.dto.feature.CreateFeatureDTO;
import ru.example.lam.server.dto.feature.EditFeatureDTO;
import ru.example.lam.server.dto.feature.FeatureRecordDTO;
import ru.example.lam.server.dto.feature.FeatureTableDTO;
import ru.example.lam.server.dto.sup.CreateSupDTO;
import ru.example.lam.server.dto.sup.SupRecordDTO;
import ru.example.lam.server.dto.sup.SupTableDTO;
import ru.example.lam.server.dto.journal.AddJournalDTO;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.dto.journal.JournalTableDTO;
import ru.example.lam.server.dto.monitor.AddMonitorDTO;
import ru.example.lam.server.dto.monitor.MonitorRecordDTO;
import ru.example.lam.server.dto.monitor.MonitorTableDTO;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;
import ru.example.lam.server.model.feature.FeatureRequest;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.model.journal.JournalTableRequest;
import ru.example.lam.server.model.monitor.MonitorRequest;
import ru.example.lam.server.model.sup.SupObjectRequest;

/**
 * Класс конфигурации для MapStruct
 */
@Mapper(componentModel = "spring")
public interface DtoMapper {

    JournalTableDTO toJournalTableDto(JournalTableRequest journalTableRequest);
    JournalRecordDTO toJournalDto(JournalRecordRequestWithId journalRecordRequestWithId);
    JournalRecordDTO fromAddToJournalRecordDto(AddJournalDTO addJournalDTO);
    AuditTableDTO auditTableRequestToDto(AuditTableRequest auditTableRequest);
    AuditRecordDTO toAuditRecordDto(AuditRecordRequestWithId auditRecordRequestWithId);
    AuditRecordDTO fromAddToAuditRecordDto(AddAuditDTO addAuditDTO);
    SupTableDTO toSupTableDto(SupObjectRequest supObjectRequest);
    SupRecordDTO fromCreateToSupRecordDto(CreateSupDTO createSupDTO);
    SupRecordDTO fromRequestToSupRecord(SupObjectRequest supObjectRequest);
    MonitorTableDTO toMonitorTableDto(MonitorRequest monitorRequest);
    MonitorTableDTO fromCreateToMonitorRecordDto(AddMonitorDTO addMonitorDTO);
    MonitorRecordDTO fromRequestToMonitorRecord(MonitorRequest monitorRequest);
    FeatureTableDTO toFeatureTableDto(FeatureRequest featureRequest);
    FeatureRecordDTO fromCreateToFeatureRecordDto(CreateFeatureDTO createFeatureDTO);
    FeatureRecordDTO fromEditToFeatureRecordDto(EditFeatureDTO editFeatureDTO);
    FeatureRecordDTO fromRequestToFeatureRecordDto(FeatureRequest featureRequest);
}
