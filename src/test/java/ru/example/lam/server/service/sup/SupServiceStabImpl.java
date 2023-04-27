package ru.example.lam.server.service.sup;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ru.example.lam.server.dto.sup.enums.SupParameterType;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.sup.CreateSupDTO;
import ru.example.lam.server.dto.sup.SupRecordDTO;
import ru.example.lam.server.dto.sup.SupTableDTO;
import ru.example.lam.server.dto.sup.EditSupDTO;
import ru.example.lam.server.dto.sup.LastVersionDTO;
import ru.example.lam.server.model.sup.SupObjectRequest;
import ru.example.lam.server.service.mapping.DtoMapper;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Временная "заглушка" для тестовой реализации {@link ISupService}
 */
@Service
@Profile("test")
public class SupServiceStabImpl implements ISupService {
    public static String EMPTY_REQUEST_BODY_MESSAGE = "request body is empty";
    public static String NOT_FOUND_RECORD_MESSAGE = "Record with id %d not found";
    @Getter
    SupRecordDTO supRecordDTO;
    @Autowired
    private DtoMapper dtoMapper;

    @Override
    public SupTableDTO getSupTable(SupObjectRequest supObjectRequest) {

        return dtoMapper.toSupTableDto(supObjectRequest);
    }

    @Override
    public SupRecordDTO getSupRecordByID(Long supRecordId) throws LamServerException {
        if (supRecordId < 1)
            throw new LamServerException("The 'id' is less than 1");
        if (supRecordId > 400)
            throw new LamServerException("Record with this id does not exist");
        return getTestSupRecordDTO();
    }

    @Override
    public boolean deleteSupRecordById(Long supRecordId) throws LamServerException {
        if (supRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        return supRecordId <= 400;
    }

    @Override
    public SupRecordDTO getSupRecordByFullPath(String fullPath) throws LamServerException {
        if (fullPath.length() < 3)
            throw new LamServerException("Parameter fullPath is invalid");
        return getTestSupRecordDTO();
    }

    @Override
    public SupRecordDTO createSupRecord(CreateSupDTO createSupDTO) throws LamServerException {
        if (createSupDTO == null)
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        return dtoMapper.fromCreateToSupRecordDto(createSupDTO);
    }

    @Override
    public SupRecordDTO editSupRecord(EditSupDTO editSupDTO) throws LamServerException {
        if (editSupDTO == null)
            throw new LamServerException(EMPTY_REQUEST_BODY_MESSAGE);
        supRecordDTO = SupRecordDTO.builder()
                .id(209L)
                .parameterName("name")
                .parameterValueList(Arrays.asList("as", "df"))
                .comment("old comment")
                .build();
        if (supRecordDTO.getId().equals(editSupDTO.getId())) {
            supRecordDTO.setParameterName(editSupDTO.getParameterName());
            supRecordDTO.setParameterValueList(editSupDTO.getParameterValueList());
            supRecordDTO.setComment(editSupDTO.getComment());
        } else {
            throw new LamServerException(String.format(NOT_FOUND_RECORD_MESSAGE, editSupDTO.getId()));
        }
        return supRecordDTO;
    }

    @Override
    public SupRecordDTO getSupRecordByIdWithSort(Long supRecordId, SupObjectRequest supObjectRequest) throws LamServerException {
        if (supRecordId < 1) {
            throw new LamServerException("The 'id' is less than 1");
        }
        if (supRecordId > 400) {
            throw new LamServerException("Record with this id does not exist");
        }

        SupRecordDTO supRecordDTO = dtoMapper.fromRequestToSupRecord(supObjectRequest);
        supRecordDTO.setId(supRecordId);
        return supRecordDTO;
    }

    public SupRecordDTO getTestSupRecordDTO() {
        return SupRecordDTO.builder()
                .id(200L)
                .fullPath("fullpath")
                .parameter("parameter value")
                .parameterName("param name")
                .subSystem("sub system value")
                .supParameterType(SupParameterType.ARRAY)
                .version("2.0.0")
                .username("user")
                .parameterValueList(Arrays.asList("1", "2", "3"))
                .createDatetime(LocalDateTime.of(2010, 5, 15, 22, 10, 20))
                .updateTime(LocalDateTime.of(2015, 10, 15, 1, 10, 20))
                .comment("test object")
                .lastVersion(LastVersionDTO.builder()
                        .version("version")
                        .parameterValueList(Arrays.asList("a", "b"))
                        .updateTime(LocalDateTime.of(208, 6, 15, 2, 10, 20))
                        .username("admin")
                        .comment("fix")
                        .build())
                .build();
    }
}
