package ru.example.lam.server.service.feature;

import ru.example.lam.server.controller.feature.FeatureController;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.feature.CreateFeatureDTO;
import ru.example.lam.server.dto.feature.EditFeatureDTO;
import ru.example.lam.server.dto.feature.FeatureRecordDTO;
import ru.example.lam.server.dto.feature.FeatureTableDTO;
import ru.example.lam.server.model.feature.FeatureRequest;

/**
 * Интерфейс для взаимодействия {@link FeatureController} с БД
 */
public interface IFeatureService {

    /**
     * Получение таблицы Feature согласно входящим параметрам
     *
     * @param featureRequest POJO с параметрами запроса
     * @return DTO с таблицей Feature {@link FeatureTableDTO}
     */
    FeatureTableDTO getFeatureTable(FeatureRequest featureRequest);

    /**
     * Получение записи Feature по id
     *
     * @param featureRecordId id записи Feature
     * @return DTO с записью Feature {@link FeatureRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    FeatureRecordDTO getFeatureRecordById(Long featureRecordId) throws LamServerException;

    /**
     * Удаление записи Feature по id
     *
     * @param featureRecordId id записи Feature
     * @return boolean значение успешности запроса
     * @throws LamServerException исключение с причиной провала операции
     */
    boolean deleteFeatureRecordById(Long featureRecordId) throws LamServerException;

    /**
     * Получение записи Feature по наименованию, версии, подсистеме
     *
     * @param featureName   наименование записи Feature
     * @param subsystemName система записи Feature
     * @param version       версия записи Feature
     * @return DTO с записью Feature {@link FeatureRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    FeatureRecordDTO getFeatureRecordByNameAndSubSystemAndVersion(String featureName,
                                                                  String subsystemName, String version) throws LamServerException;

    /**
     * Добавление записи Feature в БД
     *
     * @param createFeatureDTO DTO с записью Feature для добавления в БД
     * @return DTO с записью Feature {@link FeatureRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    FeatureRecordDTO createFeatureRecord(CreateFeatureDTO createFeatureDTO) throws LamServerException;

    /**
     * Изменение записи Feature
     *
     * @param editFeatureDTO изменения для целевой записи
     * @return DTO с записью Feature {@link FeatureRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    FeatureRecordDTO editFeatureRecord(EditFeatureDTO editFeatureDTO) throws LamServerException;

    /**
     * Получение записи Feature согласно входящим параметрам
     *
     * @param featureRecordId id записи Feature
     * @param featureRequest  POJO с параметрами запроса
     * @return DTO с записью Feature {@link FeatureRecordDTO}
     * @throws LamServerException исключение с причиной провала операции
     */
    FeatureRecordDTO getFeatureRecordFromTable(Long featureRecordId, FeatureRequest featureRequest) throws LamServerException;

}
