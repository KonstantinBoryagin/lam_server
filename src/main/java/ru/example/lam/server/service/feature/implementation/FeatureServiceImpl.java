package ru.example.lam.server.service.feature.implementation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.feature.CreateFeatureDTO;
import ru.example.lam.server.dto.feature.EditFeatureDTO;
import ru.example.lam.server.dto.feature.FeatureRecordDTO;
import ru.example.lam.server.dto.feature.FeatureTableDTO;
import ru.example.lam.server.model.feature.FeatureRequest;
import ru.example.lam.server.service.feature.IFeatureService;

/**
 * Реализация интерфейса {@link IFeatureService}
 */
@Service
@Profile({"dev", "devs", "prod"})
public class FeatureServiceImpl implements IFeatureService {
    @Override
    public FeatureTableDTO getFeatureTable(FeatureRequest featureRequest) {
        return null;
    }

    @Override
    public FeatureRecordDTO getFeatureRecordById(Long featureRecordId) {
        return null;
    }

    @Override
    public boolean deleteFeatureRecordById(Long featureRecordId) {
        return false;
    }

    @Override
    public FeatureRecordDTO getFeatureRecordByNameAndSubSystemAndVersion(String featureName, String subsystemName, String version) {
        return null;
    }

    @Override
    public FeatureRecordDTO createFeatureRecord(CreateFeatureDTO createFeatureDTO) {
        return null;
    }

    @Override
    public FeatureRecordDTO editFeatureRecord(EditFeatureDTO editFeatureDTO) {
        return null;
    }

    @Override
    public FeatureRecordDTO getFeatureRecordFromTable(Long featureRecordId, FeatureRequest featureRequest) {
        return null;
    }
}
