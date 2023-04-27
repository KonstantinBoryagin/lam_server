package ru.example.lam.server.service.monitor.implementation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.monitor.AddMonitorDTO;
import ru.example.lam.server.dto.monitor.MonitorChartDTO;
import ru.example.lam.server.dto.monitor.MonitorRecordDTO;
import ru.example.lam.server.dto.monitor.MonitorTableDTO;
import ru.example.lam.server.model.monitor.MonitorRequest;
import ru.example.lam.server.service.monitor.IMonitorService;

/**
 * Реализация {@link IMonitorService}
 */
@Service
@Profile({"dev", "devs", "prod"})
public class MonitorService implements IMonitorService {

    @Override
    public MonitorTableDTO getMonitorTable(MonitorRequest monitorRequest) {
        return null;
    }

    @Override
    public MonitorRecordDTO getMonitorRecordById(Long monitorRecordId) {
        return null;
    }

    @Override
    public boolean deleteMonitorRecordById(Long monitorRecordId) {
        return false;
    }

    @Override
    public MonitorRecordDTO getMonitorRecordByServiceNameAndVersion(String serviceName, String version) {
        return null;
    }

    @Override
    public MonitorRecordDTO addMonitorRecord(AddMonitorDTO addMonitorDTO) {
        return null;
    }

    @Override
    public MonitorRecordDTO putRecordToMonitorChart(Long monitorRecordId, MonitorChartDTO monitorChartDTO) {
        return null;
    }

    @Override
    public MonitorRecordDTO hideOrShowMonitorRecord(Long monitorRecordId, boolean hide) {
        return null;
    }

    @Override
    public MonitorRecordDTO getMonitorRecordFromTable(Long monitorRecordId, MonitorRequest monitorRequest) {
        return null;
    }
}
