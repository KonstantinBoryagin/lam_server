package ru.example.lam.server.service.sup.implementation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.sup.CreateSupDTO;
import ru.example.lam.server.dto.sup.EditSupDTO;
import ru.example.lam.server.dto.sup.SupRecordDTO;
import ru.example.lam.server.dto.sup.SupTableDTO;
import ru.example.lam.server.model.sup.SupObjectRequest;
import ru.example.lam.server.service.sup.ISupService;

/**
 * Реализация {@link ISupService}
 */
@Service
@Profile({"dev", "devs", "prod"})
public class SupService implements ISupService {

    @Override
    public SupTableDTO getSupTable(SupObjectRequest supObjectRequest) {
        return null;
    }

    @Override
    public SupRecordDTO getSupRecordByID(Long supRecordId) {
        return null;
    }

    @Override
    public boolean deleteSupRecordById(Long supRecordId) {
        return false;
    }

    @Override
    public SupRecordDTO getSupRecordByFullPath(String fullPath) {
        return null;
    }

    @Override
    public SupRecordDTO createSupRecord(CreateSupDTO createSupDTO) {
        return null;
    }

    @Override
    public SupRecordDTO editSupRecord(EditSupDTO editSupDTO) {
        return null;
    }

    @Override
    public SupRecordDTO getSupRecordByIdWithSort(Long supRecordId, SupObjectRequest supObjectRequest) {
        return null;
    }
}
