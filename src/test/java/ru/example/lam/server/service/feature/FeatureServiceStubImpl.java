package ru.example.lam.server.service.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.feature.CreateFeatureDTO;
import ru.example.lam.server.dto.feature.EditFeatureDTO;
import ru.example.lam.server.dto.feature.FeatureRecordDTO;
import ru.example.lam.server.dto.feature.FeatureTableDTO;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.model.feature.FeatureRequest;
import ru.example.lam.server.service.mapping.DtoMapper;

import java.time.LocalDateTime;

/**
 * Временная "заглушка" для тестовой реализации {@link IFeatureService}
 */
@Service
@Profile("test")
public class FeatureServiceStubImpl implements IFeatureService {

    public static final String EMPTY_REQUEST_BODY_MESSAGE = "Request body is empty";
    public static final String NOT_FOUND_RECORD_MESSAGE = "Record with id %d not found";
    public static final String NOT_FOUND_VERSION_RECORD_MESSAGE = "Record with version %s not found";
    public static final String INVALID_ID_MESSAGE = "The 'id' is less than 1";

    @Autowired
    DtoMapper dtoMapper;

    @Override
    public FeatureTableDTO getFeatureTable(FeatureRequest featureRequest) {
        return dtoMapper.toFeatureTableDto(featureRequest);
    }

    @Override
    public FeatureRecordDTO getFeatureRecordById(Long featureRecordId) throws LamServerException {
        if (featureRecordId < 1)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (featureRecordId > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, featureRecordId));
        return getTestFeatureRecord(featureRecordId);
    }

    @Override
    public boolean deleteFeatureRecordById(Long featureRecordId) throws LamServerException {
        if (featureRecordId < 1) {
            throw new LamServerException(INVALID_ID_MESSAGE);
        }
        return featureRecordId <= 400;
    }

    @Override
    public FeatureRecordDTO getFeatureRecordByNameAndSubSystemAndVersion(String featureName, String subsystemName,
                                                                         String version) throws LamServerException {
        if (version.equals("1.0"))
            throw new LamServerException(String.format(NOT_FOUND_VERSION_RECORD_MESSAGE, version));
        return getTestFeatureRecord(featureName, subsystemName, version);
    }

    @Override
    public FeatureRecordDTO createFeatureRecord(CreateFeatureDTO createFeatureDTO) throws LamServerException {
        if (createFeatureDTO == null)
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        return dtoMapper.fromCreateToFeatureRecordDto(createFeatureDTO);
    }

    @Override
    public FeatureRecordDTO editFeatureRecord(EditFeatureDTO editFeatureDTO) throws LamServerException {
        if (editFeatureDTO == null)
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        if (editFeatureDTO.getId() < 0)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (editFeatureDTO.getId() > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, editFeatureDTO.getId()));
        return dtoMapper.fromEditToFeatureRecordDto(editFeatureDTO);
    }

    @Override
    public FeatureRecordDTO getFeatureRecordFromTable(Long featureRecordId, FeatureRequest featureRequest) throws LamServerException {
        if (featureRecordId < 0)
            throw new LamServerException(INVALID_ID_MESSAGE);
        if (featureRecordId > 400)
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, featureRecordId));
        FeatureRecordDTO featureRecordDTO = dtoMapper.fromRequestToFeatureRecordDto(featureRequest);
        featureRecordDTO.setId(featureRecordId);
        return featureRecordDTO;
    }

    public FeatureRecordDTO getTestFeatureRecord(Long id) {
        return FeatureRecordDTO.builder()
                .id(id)
                .username("admin")
                .comment("comment from admin")
                .featureName("bug fix")
                .featureStatus(true)
                .subsystem("LAM core")
                .description("bug fix for LAM core")
                .updateTime(LocalDateTime.of(2012, 12, 30, 23, 55, 10))
                .version("0.0.1 LAST")
                .build();
    }

    public FeatureRecordDTO getTestFeatureRecord(String featureName, String subsystemName, String version) {
        return FeatureRecordDTO.builder()
                .id(199L)
                .username("admin")
                .comment("comment from admin")
                .featureName(featureName)
                .featureStatus(true)
                .subsystem(subsystemName)
                .description("bug fix for LAM core")
                .updateTime(LocalDateTime.of(2022, 3, 5, 14, 55,10))
                .version(version)
                .build();
    }
}
