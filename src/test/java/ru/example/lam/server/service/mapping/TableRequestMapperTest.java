package ru.example.lam.server.service.mapping;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.example.lam.server.model.enums.Position.POSITION_EXCEPTION_MESSAGE;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.dto.sorting.ItemDTO;
import ru.example.lam.server.dto.sorting.TagDTO;
import ru.example.lam.server.model.JournalPojo;
import ru.example.lam.server.model.audit.AuditRecordRequestWithId;
import ru.example.lam.server.model.audit.AuditTableRequest;
import ru.example.lam.server.model.journal.JournalRecordRequestWithId;
import ru.example.lam.server.model.journal.JournalTableRequest;
import ru.example.lam.server.model.enums.Position;

/**
 * Класс тестов для {@link TableRequestMapper}
 */
@BootTest
class TableRequestMapperTest {

    @Autowired
    TableRequestMapper tableRequestMapper;
    JournalRecordRequestWithId actualJournalRecordRequestWithId;
    JournalRecordRequestWithId expectedJournalRecordRequestWithId;
    AuditRecordRequestWithId expectedAuditRecordRequestWithId;
    AuditRecordRequestWithId actualAuditRecordRequestWithId;
    JournalTableRequest expectedJournalTableRequest;
    JournalTableRequest actualJournalTableRequest;
    AuditTableRequest expectedAuditTableRequest;
    AuditTableRequest actualAuditTableRequest;
    List<String> tagsList;
    List<String> itemsList;

    @ParameterizedTest(name = "{index} test for exception in result from 'completeTheRecordRequestWithId' method")
    @MethodSource("tagsAndItemsAndPositionForExceptionTestWithRecordData")
    void completeTheRecordRequestWithIdAndExceptionTest(String tag, String item, String exceptedMessage,
                                                        JournalRecordRequestWithId journalRecordRequestWithId,
                                                        AuditRecordRequestWithId auditRecordRequestWithId) {
        tagsList = new ArrayList<>(Collections.singletonList(tag));
        itemsList = new ArrayList<>(Collections.singletonList(item));
        String position = "next";
        actualJournalRecordRequestWithId = journalRecordRequestWithId;
        LamServerException lamServerException = assertThrows(LamServerException.class, () -> tableRequestMapper
                .completeTheRecordRequestWithId(actualJournalRecordRequestWithId,
                        tagsList, itemsList, position), "no LamServerException");
        assertEquals(exceptedMessage, lamServerException.getMessage());
        //проверка перегруженного метода
        actualAuditRecordRequestWithId = auditRecordRequestWithId;
        LamServerException lamServerException2 = assertThrows(LamServerException.class, () -> tableRequestMapper
                .completeTheRecordRequestWithId(actualAuditRecordRequestWithId,
                        tagsList, itemsList, position), "no LamServerException");
        assertEquals(exceptedMessage, lamServerException2.getMessage());
    }

    @ParameterizedTest(name = "{index} test for exception in result from 'completeTheJournalTableRequest' method")
    @MethodSource("tagsAndItemsAndPositionForExceptionTestWithTableData")
    void completeTheTableRequestWithExceptionTest(String tag, String item, String expectedMessage,
                                                  JournalTableRequest journalTableRequest,
                                                  AuditTableRequest auditTableRequest) {
        tagsList = new ArrayList<>(Collections.singletonList(tag));
        itemsList = new ArrayList<>(Collections.singletonList(item));
        actualJournalTableRequest = journalTableRequest;
        LamServerException lamServerException = assertThrows(LamServerException.class, () -> tableRequestMapper
                .completeTheTableRequest(actualJournalTableRequest,
                        tagsList, itemsList), "no LamServerException");
        assertEquals(expectedMessage, lamServerException.getMessage());
        //проверка перегруженного метода
        actualAuditTableRequest = auditTableRequest;
        LamServerException lamServerException2 = assertThrows(LamServerException.class, () -> tableRequestMapper
                .completeTheTableRequest(actualAuditTableRequest,
                        tagsList, itemsList), "no LamServerException");
        assertEquals(expectedMessage, lamServerException2.getMessage());
    }

    @Test
    @DisplayName("test for exception from Position enum")
    void completeTheRecordRequestWithIdAndExceptionInEnumTest() {
        actualJournalRecordRequestWithId = new JournalRecordRequestWithId();
        tagsList = new ArrayList<>(Collections.singletonList("tag=tag value"));
        itemsList = new ArrayList<>(Collections.singletonList("item =item value"));
        String position = "next!";
        LamServerException lamServerException = assertThrows(LamServerException.class, () -> tableRequestMapper
                .completeTheRecordRequestWithId(actualJournalRecordRequestWithId,
                        tagsList, itemsList, position), "no LamServerException");
        assertEquals(String.format(POSITION_EXCEPTION_MESSAGE, position), lamServerException.getMessage());
    }

    @ParameterizedTest(name = "{index} test for 'completeTheTableRequest' method")
    @MethodSource("tagsAndItemsAndPositionWithTableData")
    void completeTheTableRequestTest(String tag1, String tag2, String tag3, String sortBy, String sortBy2,
                                     JournalTableRequest journalTableRequest,
                                     AuditTableRequest auditTableRequest) throws LamServerException {
        tagsList = new ArrayList<>(Arrays.asList(tag1, tag2, tag3));
        itemsList = new ArrayList<>(Arrays.asList(sortBy, sortBy2));

        actualJournalTableRequest = journalTableRequest;
        expectedJournalTableRequest = new JournalTableRequest();
        tableRequestMapper.completeTheTableRequest(actualJournalTableRequest, tagsList, itemsList);
        formExpectedObject(expectedJournalTableRequest, tagsList, itemsList);
        assertEquals(
                expectedJournalTableRequest,
                actualJournalTableRequest
        );
        //проверка перегруженного метода
        actualAuditTableRequest = auditTableRequest;
        expectedAuditTableRequest = new AuditTableRequest();
        tableRequestMapper.completeTheTableRequest(actualAuditTableRequest, tagsList, itemsList);
        formExpectedObject(expectedAuditTableRequest, tagsList, itemsList);
        assertEquals(
                expectedAuditTableRequest,
                actualAuditTableRequest
        );
    }

    @ParameterizedTest(name = "{index} test for 'completeRecordRequestWithId' method")
    @MethodSource("tagsAndItemsAndPositionWithRecordData")
    void completeTheRecordRequestWithIdTest(String tag1, String tag2, String tag3, String sortBy, String sortBy2,
                                            JournalRecordRequestWithId journalRecordRequestWithId,
                                            AuditRecordRequestWithId auditRecordRequestWithId,
                                            String position) throws LamServerException {
        tagsList = new ArrayList<>(Arrays.asList(tag1, tag2, tag3));
        itemsList = new ArrayList<>(Arrays.asList(sortBy, sortBy2));

        actualJournalRecordRequestWithId = journalRecordRequestWithId;
        expectedJournalRecordRequestWithId = new JournalRecordRequestWithId();
        tableRequestMapper.completeTheRecordRequestWithId(actualJournalRecordRequestWithId, tagsList,
                itemsList, position);
        formExpectedObject(expectedJournalRecordRequestWithId, tagsList, itemsList);
        expectedJournalRecordRequestWithId.setPositionValue(Position.getPosition(position));
        assertEquals(
                expectedJournalRecordRequestWithId,
                actualJournalRecordRequestWithId
        );
        //проверка перегруженного метода
        actualAuditRecordRequestWithId = auditRecordRequestWithId;
        expectedAuditRecordRequestWithId = new AuditRecordRequestWithId();
        tableRequestMapper.completeTheRecordRequestWithId(actualAuditRecordRequestWithId, tagsList,
                itemsList, position);
        formExpectedObject(expectedAuditRecordRequestWithId, tagsList, itemsList);
        expectedAuditRecordRequestWithId.setPositionValue(Position.getPosition(position));
        assertEquals(
                expectedAuditRecordRequestWithId,
                actualAuditRecordRequestWithId
        );
    }

    public static Stream<Arguments> tagsAndItemsAndPositionWithRecordData() {
        return Stream.of(
                arguments("tag_1=", "tag_1=value 2", "tag_1=value 3", "type_1=descending", "type_1=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "next"),
                arguments("tag_2=value 1", "tag_2=value 2", "tag_2=value 3", "type_2=descending", "type_2=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "next"),
                arguments("tag_3=", "tag_3=value 2", "tag_3=value 3", "type_3=descending", "type_3=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "next"),
                arguments("tag_4=value 1", "tag_4=value 2", "tag_4=value 3", "type_4=descending", "type_4=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "current"),
                arguments("tag_5=", "tag_5=value 2", "tag_5=value 3", "type_5=descending", "type_5=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "previous"),
                arguments("tag_6=value 1", "tag_6=value 2", "tag_6=value 3", "type_6=descending", "type_6=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "previous"),
                arguments("tag_7", "tag_7=value 2", "tag_7=value 3", "type_7=descending", "type_7=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "current"),
                arguments("tag_8=value 1", "tag_8=value 2", "tag_8=value 3", "type_8=descending", "type_8=ascending",
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId(), "current")
        );
    }

    public static Stream<Arguments> tagsAndItemsAndPositionWithTableData() {
        return Stream.of(
                arguments("tag_1=", "tag_1=value 2", "tag_1=value 3", "type_1=descending", "type_1=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "next"),
                arguments("tag_2=value 1", "tag_2=value 2", "tag_2=value 3", "type_2=descending", "type_2=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "next"),
                arguments("tag_3=", "tag_3=value 2", "tag_3=value 3", "type_3=descending", "type_3=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "next"),
                arguments("tag_4=value 1", "tag_4=value 2", "tag_4=value 3", "type_4=descending", "type_4=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "current"),
                arguments("tag_5=", "tag_5=value 2", "tag_5=value 3", "type_5=descending", "type_5=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "previous"),
                arguments("tag_6=value 1", "tag_6=value 2", "tag_6=value 3", "type_6=descending", "type_6=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "previous"),
                arguments("tag_7", "tag_7=value 2", "tag_7=value 3", "type_7=descending", "type_7=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "current"),
                arguments("tag_8=value 1", "tag_8=value 2", "tag_8=value 3", "type_8=descending", "type_8=ascending",
                        new JournalTableRequest(), new AuditTableRequest(), "current")
        );
    }

    public static Stream<Arguments> tagsAndItemsAndPositionForExceptionTestWithRecordData() {
        return Stream.of(
                arguments("tag=tag value", "item item value", TableRequestMapper.ITEM_EXCEPTION_MESSAGE,
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId()),
                arguments("tag tag value", "item @ item value", TableRequestMapper.ITEM_EXCEPTION_MESSAGE,
                        new JournalRecordRequestWithId(), new AuditRecordRequestWithId())
        );
    }

    public static Stream<Arguments> tagsAndItemsAndPositionForExceptionTestWithTableData() {
        return Stream.of(
                arguments("tag=tag value", "item item value", TableRequestMapper.ITEM_EXCEPTION_MESSAGE,
                        new JournalTableRequest(), new AuditTableRequest()),
                arguments("tag tag value", "item @ item value", TableRequestMapper.ITEM_EXCEPTION_MESSAGE,
                        new JournalTableRequest(), new AuditTableRequest())
        );
    }

    static void formExpectedObject(JournalPojo journalPojo, List<String> tagsList,
                                   List<String> itemsList) {
        List<TagDTO> tagDtoList = new ArrayList<>();
        for (String s : tagsList) {
            String[] values = s.split(TableRequestMapper.PARAMETER_SEPARATOR);
            if (values.length > 0) {
                tagDtoList.add(TagDTO.builder()
                        .name(values[0].trim())
                        .value(values.length == 2 ? values[1].trim() : "")
                        .build());
            }
        }
        journalPojo.setTagsDtoList(tagDtoList);

        List<ItemDTO> itemDtoList = new ArrayList<>();

        for (String s : itemsList) {
            String[] values = s.split(TableRequestMapper.PARAMETER_SEPARATOR);
            if (values.length != 2) {
                throw new IllegalArgumentException("parameter 'tags' is invalid");
            }

            itemDtoList.add(ItemDTO.builder()
                    .type(values[0].trim())
                    .direction(values[1].trim())
                    .build());
        }
        journalPojo.setItemsDtoList(itemDtoList);
    }

}