package ru.example.lam.server.controller.security;

import com.tngtech.keycloakmock.api.KeycloakMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static com.tngtech.keycloakmock.api.ServerConfig.aServerConfig;
import static com.tngtech.keycloakmock.api.TokenConfig.aTokenConfig;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.service.audit.IAuditService;
import ru.example.lam.server.service.feature.IFeatureService;
import ru.example.lam.server.service.journal.IJournalService;
import ru.example.lam.server.service.monitor.IMonitorService;
import ru.example.lam.server.service.sup.ISupService;
import ru.example.lam.server.service.users.IUsersService;

/**
 * Тестовый класс для проверки сценариев безопасности и доступа на основе роли во все контролеры
 * с использованием встроенного(mock) Keycloak
 */
@ActiveProfiles("test")
@BootTest
@SpringBootTest(properties = {"spring.liquibase.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTest {

    private final static String ACTUATOR_URI = "/actuator";
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String TEST_ROLE = "TEST";
    //совпадают с одноименным параметром из тестовых свойств, но вытащить через @Value не получается, не успевает "поднять"
    private static final String KEYCLOAK_REALM = "Lam-test";
    private static final int KEYCLOAK_PORT = 8000;

    @Value("${keycloak.resource}")
    private String keycloakResource;

    private final KeycloakMock mock = new KeycloakMock(aServerConfig().withPort(KEYCLOAK_PORT).withDefaultRealm(KEYCLOAK_REALM).build());

    @LocalServerPort
    private int port;

    @MockBean
    private IUsersService userService;
    @MockBean
    private IMonitorService monitorService;
    @MockBean
    private ISupService supService;
    @MockBean
    private IJournalService journalService;
    @MockBean
    private IFeatureService featureService;
    @MockBean
    private IAuditService auditService;

    @BeforeEach
    void setup() {
        //запускаем Keycloak
        mock.start();
        //в случае провала теста выведет подробную информацию из RestAssured
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
    }

    @AfterEach
    void stop() {
        //останавливаем Keycloak
        mock.stop();
    }

    @Test
    void withoutAuthenticationTest() {
        RestAssured.given()
                .when().get(ACTUATOR_URI)
                .then().statusCode(HttpStatus.OK.value());
    }

    @ParameterizedTest(name = "{index} test for no authentication fails")
    @MethodSource("controllersRootAndTablesUri")
    void noAuthenticationFails(String uri) {
        RestAssured.given()
                .when().get(uri)
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest(name = "{index} test for authentication works")
    @MethodSource("controllersRootAndTablesUri")
    void authenticationWorks(String uri) {
        RestAssured.given()
                .auth()
                .preemptive()
                .oauth2(mock.getAccessToken(aTokenConfig().withResourceRole(keycloakResource, USER_ROLE).build()))
                .when()
                .get(uri)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @ParameterizedTest(name = "{index} test for authentication without role fails")
    @MethodSource("controllersRootAndTablesUri")
    void authenticationWithoutRoleFails(String uri) {
        RestAssured.given()
                .auth().preemptive().oauth2(mock.getAccessToken(aTokenConfig().build()))
                .when().get(uri)
                .then().statusCode(HttpStatus.FORBIDDEN.value());
    }

    @ParameterizedTest(name = "{index} test for authentication with wrong role fails")
    @MethodSource("controllersRootAndTablesUri")
    void authenticationWithWrongRoleFails(String uri) {
        RestAssured.given()
                .auth()
                .preemptive()
                .oauth2(mock.getAccessToken(aTokenConfig().withResourceRole(keycloakResource, TEST_ROLE).build()))
                .when()
                .get(uri)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @ParameterizedTest(name = "{index} test for authentication with invalid role fails")
    @MethodSource("controllersRootAndRecordIdUri")
    void authenticationWithInvalidRoleFails(String uri) {
        long id = 1;
        RestAssured.given()
                .auth()
                .preemptive()
                .oauth2(mock.getAccessToken(aTokenConfig().withResourceRole(keycloakResource, USER_ROLE).build()))
                .when()
                .delete(uri, id)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @ParameterizedTest(name = "{index} test for authentication with correct role works")
    @MethodSource("controllersRootAndRecordIdUri")
    void authenticationWithCorrectRoleWorks(String uri) throws Exception{
        long id = 1;
        //мокаем сервисы
        Mockito.when(userService.deleteUserById(id)).thenReturn(true);
        Mockito.when(monitorService.deleteMonitorRecordById(id)).thenReturn(true);
        Mockito.when(supService.deleteSupRecordById(id)).thenReturn(true);
        Mockito.when(journalService.deleteJournalRecordById(id)).thenReturn(true);
        Mockito.when(featureService.deleteFeatureRecordById(id)).thenReturn(true);
        Mockito.when(auditService.deleteAuditRecordById(id)).thenReturn(true);

        RestAssured.given()
                .auth()
                .preemptive()
                .oauth2(mock.getAccessToken(aTokenConfig().withResourceRole(keycloakResource, ADMIN_ROLE).build()))
                .when()
                .delete(uri, id)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    public static Stream<Arguments> controllersRootAndTablesUri() {
        return Stream.of(
                arguments("/api/audit/audittable"),
                arguments("/api/feature/featuretable"),
                arguments("/api/journal/journaltable"),
                arguments("/api/monitor/monitortable"),
                arguments("/api/sup/suptable"),
                arguments("/user/all")
        );
    }

    public static Stream<Arguments> controllersRootAndRecordIdUri() {
        return Stream.of(
                arguments("/api/audit/{auditrecordid}"),
                arguments("/api/feature/{featurerecordid}"),
                arguments("/api/journal/{journalrecordid}"),
                arguments("/api/monitor/{monitorrecordid}"),
                arguments("/api/sup/{suprecordid}"),
                arguments("/user/{userid}")
        );
    }
}
