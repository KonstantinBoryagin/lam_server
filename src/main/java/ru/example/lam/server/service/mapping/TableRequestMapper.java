package ru.example.lam.server.service.mapping;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.sorting.ItemDTO;
import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.model.JournalPojo;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;
import ru.example.lam.server.model.feature.FeatureRequest;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.model.journal.JournalTableRequest;
import ru.example.lam.server.model.enums.Position;
import ru.example.lam.server.model.monitor.MonitorRequest;
import ru.example.lam.server.model.sup.SupObjectRequest;

/**
 * Класс-mapper для добавления к запросу таблицы/записи в журнале относительной позиции, ее списков тегов и сортировки
 */
@Service
@Log4j2
public class TableRequestMapper {

    /**
     * Разделитель для параметров 'tags' и 'sortby'
     */
    public static final String PARAMETER_SEPARATOR = "=";
    public static final String ITEM_EXCEPTION_MESSAGE = "parameter 'sortby' is invalid";

    /**
     * Дополняет запрос записи в журнале параметрами: относительная позиция, теги, сортировка
     *
     * @param journalRecordRequestWithId запрос записи в журнале для модификации
     * @param position                   относительная позиция записи в журнале
     * @param tagsList                   List тегов
     * @param itemsList                  List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheRecordRequestWithId(JournalRecordRequestWithId journalRecordRequestWithId,
                                               List<String> tagsList, List<String> itemsList,
                                               String position) throws LamServerException {
        log.debug("Method completeTheRecordRequestWithId is stared");
        journalRecordRequestWithId.setPositionValue(Position.getPosition(position));
        log.debug("Position value is set {}", journalRecordRequestWithId);
        setListToPojo(tagsList, itemsList, journalRecordRequestWithId);
        log.debug("List to pojo is set {}", journalRecordRequestWithId);
    }

    /**
     * Дополняет запрос записи в журнале параметрами: относительная позиция, теги, сортировка
     *
     * @param auditRecordRequestWithId запрос записи в журнале аудита для модификации
     * @param position                 относительная позиция записи в журнале
     * @param tagsList                 List тегов
     * @param itemsList                List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheRecordRequestWithId(AuditRecordRequestWithId auditRecordRequestWithId,
                                               List<String> tagsList, List<String> itemsList,
                                               String position) throws LamServerException {
        log.debug("Method completeTheRecordRequestWithId is started");
        auditRecordRequestWithId.setPositionValue(Position.getPosition(position));
        log.debug("Position value is set {}", auditRecordRequestWithId);
        setListToPojo(tagsList, itemsList, auditRecordRequestWithId);
        log.debug("List to pojo is set {}", auditRecordRequestWithId);
    }

    /**
     * Дополняет запрос записи в СУП параметрами: относительная позиция, теги, сортировка
     *
     * @param monitorRequest запрос записи в мониторинге событий для модификации
     * @param itemsList      List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheRecordRequestWithId(MonitorRequest monitorRequest,
                                               List<String> itemsList) throws LamServerException {
        log.debug("Method completeTheRecordRequestWithId is started");
        if (itemsList != null && !itemsList.isEmpty())
            monitorRequest.setItemsList(formItemDtoList(itemsList));
        log.debug("Items list is set {}", monitorRequest);
    }

    /**
     * Дополняет запрос записи в СУП параметрами: относительная позиция, теги, сортировка
     *
     * @param supObjectRequest запрос записи в журнале аудита для модификации
     * @param position         относительная позиция записи в журнале
     * @param itemsList        List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheRecordRequestWithId(SupObjectRequest supObjectRequest,
                                               List<String> itemsList,
                                               String position) throws LamServerException {
        log.debug("Method completeTheRecordRequestWithId is started");
        supObjectRequest.setPositionValue(Position.getPosition(position));
        log.debug("Position value is set {}", supObjectRequest);
        setListToPojo(itemsList, supObjectRequest);
        log.debug("List to pojo is set {}", supObjectRequest);
    }

    /**
     * Дополняет запрос таблицы в журнале параметрами: теги, сортировка
     *
     * @param journalTableRequest таблица журнала для модификации
     * @param tagsList            List тегов
     * @param itemsList           List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheTableRequest(JournalTableRequest journalTableRequest,
                                        List<String> tagsList, List<String> itemsList) throws LamServerException {
        log.debug("Method completeTheTableReques is started");
        setListToPojo(tagsList, itemsList, journalTableRequest);
        log.debug("List to pojo is set {}", journalTableRequest);
    }

    /**
     * Дополняет запрос таблицы в журнале параметрами: теги, сортировка
     *
     * @param auditTableRequest таблица журнала аудита для модификации
     * @param tagsList          List тегов
     * @param itemsList         List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheTableRequest(AuditTableRequest auditTableRequest,
                                        List<String> tagsList, List<String> itemsList) throws LamServerException {
        log.debug("Method completeTheTableRequest is started");
        setListToPojo(tagsList, itemsList, auditTableRequest);
        log.debug("List to pojo is set {}", auditTableRequest);
    }

    /**
     * Дополняет запрос таблицы в журнале параметрами сортировки
     *
     * @param supObjectRequest запись СУП для модификации
     * @param itemsList        List с параметрами сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheTableRequest(SupObjectRequest supObjectRequest, List<String> itemsList) throws LamServerException {
        log.debug("Method completeTheTableRequest is started");
        if (itemsList != null && !itemsList.isEmpty())
            supObjectRequest.setItemDTOList(formItemDtoList(itemsList));
        log.debug("Item list is set {}", supObjectRequest);
    }

    /**
     * Дополняет запрос таблицы в журнале параметрами сортировки
     *
     * @param monitorRequest запись мониторинга событий для модификации
     * @param itemsList      List с параметрами сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheTableRequest(MonitorRequest monitorRequest, List<String> itemsList) throws LamServerException {
        log.debug("Method completeTheTableRequest is started");
        if (itemsList != null && !itemsList.isEmpty())
            monitorRequest.setItemsList(formItemDtoList(itemsList));
        log.debug("Items list is set {}", monitorRequest);
    }

    /**
     * Дополняет запрос таблицы в журнале параметрами сортировки
     *
     * @param featureRequest запись Feature для модификации
     * @param itemsList      List с параметрами сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    public void completeTheTableRequest(FeatureRequest featureRequest, List<String> itemsList) throws LamServerException {
        log.debug("Method completeTheTableRequest is started");
        if (itemsList != null && !itemsList.isEmpty())
            featureRequest.setItemList(formItemDtoList(itemsList));
        log.debug("Item list is set {}", featureRequest);
    }

    /**
     * Проверяет что бы коллекции не были пустые и вызывает {@link #formTagDtoList(List)}
     * и {@link #formItemDtoList(List)} для формирования списков и добавляет их к POJO сущности запроса
     *
     * @param journalTableRequest таблица журнала для модификации
     * @param tagsList            List тегов
     * @param itemsList           List параметров сортировки
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    private void setListToPojo(List<String> tagsList, List<String> itemsList, JournalPojo journalTableRequest)
            throws LamServerException {
        log.debug("Method setListToPojo is started");
        if (tagsList != null && !tagsList.isEmpty())
            journalTableRequest.setTagsDtoList(formTagDtoList(tagsList));
        log.debug("Tags list is set {}", journalTableRequest);
        if (itemsList != null && !itemsList.isEmpty())
            journalTableRequest.setItemsDtoList(formItemDtoList(itemsList));
        log.debug("Items list is set {}", journalTableRequest);
    }

    /**
     * Проверяет что бы список не был пуст и вызывает {@link #formItemDtoList(List)}
     * для формирования списка и добавляет его к POJO сущности запроса
     *
     * @param itemsList        List тегов
     * @param supObjectRequest список СУП для модификации
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    private void setListToPojo(List<String> itemsList, SupObjectRequest supObjectRequest)
            throws LamServerException {
        log.debug("Method setListToPojo is started");
        if (itemsList != null && !itemsList.isEmpty())
            supObjectRequest.setItemDTOList(formItemDtoList(itemsList));
        log.debug("Item list is set {}", supObjectRequest);
    }

    /**
     * Формирует List со значениями 'tags' для POJO запроса
     *
     * @param tagsList List тегов
     */
    private List<TagDTO> formTagDtoList(List<String> tagsList) {
        log.debug("Method formTagDtoList is started");
        List<TagDTO> tagDtoList = new ArrayList<>();

        for (String s : tagsList) {
            String[] values = s.split(PARAMETER_SEPARATOR);
            log.debug("Values splited {}", values);
            if (values.length > 0) {
                tagDtoList.add(TagDTO.builder()
                        .name(values[0].trim())
                        .value(values.length == 2 ? values[1].trim() : "")
                        .build());
            }
        }
        log.debug("List with 'tags' values formed {}", tagDtoList);
        return tagDtoList;
    }

    /**
     * @param itemsList List параметров сортировки
     * @return List из {@link ItemDTO}
     * @throws LamServerException исключение с сообщением о причине сбоя
     */
    private List<ItemDTO> formItemDtoList(List<String> itemsList) throws LamServerException {
        log.debug("Method formItemDtoList is started");
        List<ItemDTO> itemDtoList = new ArrayList<>();

        for (String s : itemsList) {
            String[] values = s.split(PARAMETER_SEPARATOR);
            log.debug("Values splited {}", values);
            if (values.length != 2) {
                log.error("Parameter 'sortby' is invalid");
                throw new LamServerException(ITEM_EXCEPTION_MESSAGE);
            }

            itemDtoList.add(ItemDTO.builder()
                    .type(values[0].trim())
                    .direction(values[1].trim())
                    .build());
        }
        log.debug("List with 'items' values formed {}", itemDtoList);
        return itemDtoList;
    }
}
