package ru.example.lam.server.service.monitor;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.monitor.AddMonitorDTO;
import ru.example.lam.server.dto.monitor.MonitorChartDTO;
import ru.example.lam.server.dto.monitor.ChartDTO;
import ru.example.lam.server.dto.monitor.MonitorTableDTO;
import ru.example.lam.server.dto.monitor.MonitorRecordDTO;
import ru.example.lam.server.dto.monitor.TimeLine;
import ru.example.lam.server.dto.monitor.enums.StatusType;
import ru.example.lam.server.model.monitor.MonitorRequest;
import ru.example.lam.server.service.mapping.DtoMapper;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * Временная "заглушка" для тестовой реализации {@link IMonitorService}
 */
@Service
@Profile("test")
public class MonitorServiceStubImpl implements IMonitorService {
    public static final String EMPTY_REQUEST_BODY_MESSAGE = "request body is empty";
    public static final String NOT_FOUND_RECORD_MESSAGE = "Record with id %d not found";
    public static final String INVALID_ID_MESSAGE = "The 'id' is less than 1";
    public static final String WRONG_VERSION_MESSAGE = "Parameter 'version' is invalid";
    @Getter
    public MonitorRecordDTO monitorRecordDTO;

    @Autowired
    private DtoMapper dtoMapper;

    @Override
    public MonitorTableDTO getMonitorTable(MonitorRequest monitorRequest) {
        return dtoMapper.toMonitorTableDto(monitorRequest);
    }

    @Override
    public MonitorRecordDTO getMonitorRecordById(Long monitorRecordId) throws LamServerException {
        if (monitorRecordId < 1)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (monitorRecordId > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, monitorRecordId));
        return getTestMonitorRecordDTO(monitorRecordId);
    }

    @Override
    public boolean deleteMonitorRecordById(Long monitorRecordId) throws LamServerException {
        if (monitorRecordId < 1) {
            throw new LamServerException(INVALID_ID_MESSAGE);
        }
        return monitorRecordId <= 400;
    }

    @Override
    public MonitorRecordDTO getMonitorRecordByServiceNameAndVersion(String serviceName, String version) throws LamServerException {
        int versionValue = Integer.parseInt(version);
        if (versionValue > 400) {
            throw new LamServerException(WRONG_VERSION_MESSAGE);
        }
        return getTestMonitorRecordDTO((long) versionValue);
    }

    @Override
    public MonitorTableDTO addMonitorRecord(AddMonitorDTO addMonitorDTO) throws LamServerException {
        if (addMonitorDTO == null)
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        return dtoMapper.fromCreateToMonitorRecordDto(addMonitorDTO);
    }

    @Override
    public MonitorRecordDTO putRecordToMonitorChart(Long monitorRecordId,
                                                    MonitorChartDTO monitorChartDTO) throws LamServerException {
        if (monitorRecordId < 1)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (monitorRecordId > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, monitorRecordId));
        if (monitorChartDTO == null)
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        monitorRecordDTO = getTestMonitorRecordDTO(monitorRecordId);
        monitorRecordDTO.setCpuLoad(monitorChartDTO.getCpuLoad());
        monitorRecordDTO.setMemoryUsage(monitorChartDTO.getMemoryUsage());
        monitorRecordDTO.setNetworkLoad(monitorChartDTO.getNetworkLoad());
        monitorRecordDTO.setWorkTime(monitorChartDTO.getWorkTime());
        return monitorRecordDTO;
    }

    @Override
    public MonitorRecordDTO hideOrShowMonitorRecord(Long monitorRecordId, boolean hide) throws LamServerException {
        if (monitorRecordId < 1)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (monitorRecordId > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, monitorRecordId));
        MonitorRecordDTO monitorRecordDTO = getTestMonitorRecordDTO(monitorRecordId);
        monitorRecordDTO.setHide(hide);
        return monitorRecordDTO;
    }

    @Override
    public MonitorRecordDTO getMonitorRecordFromTable(Long monitorRecordId, MonitorRequest monitorRequest) throws LamServerException {
        if (monitorRecordId < 1)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (monitorRecordId > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, monitorRecordId));
        MonitorRecordDTO monitorRecordDTO = dtoMapper.fromRequestToMonitorRecord(monitorRequest);
        monitorRecordDTO.setId(monitorRecordId);
        return monitorRecordDTO;
    }

    public MonitorRecordDTO getTestMonitorRecordDTO(Long id) {
        return MonitorRecordDTO.builder()
                .id(id)
                .serviceName("service name")
                .serviceAddress("service address")
                .statusType(StatusType.DEBUG)
                .updateTime("120")
                .version("3.0.0")
                .cpuLoad(getTestChartDTO("cpu load"))
                .memoryUsage(getTestChartDTO("memory usage"))
                .networkLoad(getTestChartDTO("network load"))
                .workTime(getTestChartDTO("work time"))
                .build();
    }

    public MonitorChartDTO getTestMonitorChartDTO(Long id) {
        return MonitorChartDTO.builder()
                .id(id)
                .cpuLoad(getTestChartDTO("cpu load test"))
                .memoryUsage(getTestChartDTO("memory usage test"))
                .networkLoad(getTestChartDTO("network load test"))
                .workTime(getTestChartDTO("work time test"))
                .build();
    }

    private ChartDTO getTestChartDTO(String name) {
        return ChartDTO.builder()
                .description(name)
                .currentValue("current value")
                .timeLine(TimeLine.builder()
                        .time(LocalDateTime.of(2022, Month.MAY, 18, 18, 10, 52, 555000000))
                        .value("value " + name)
                        .build())
                .build();
    }
}
