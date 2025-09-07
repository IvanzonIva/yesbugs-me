package Ivancompany.nbanktest.tests.functional.base;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.clients.AuthClient;
import Ivancompany.nbanktest.api.clients.UserAdminClient;
import Ivancompany.nbanktest.core.config.TestConfig;
import Ivancompany.nbanktest.core.services.UserTestService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class ApiTestBase {
    protected static UserAdminClient userAdminClient;
    protected static AuthClient authClient;
    protected static AccountClient accountClient;
    protected static UserTestService userTestService;

    @BeforeAll
    static void setUpApi() {
        // Устанавливаем базовый URL для RestAssured (один раз для всех тестов)
        RestAssured.baseURI = TestConfig.BASE_URL;

        // Инициализация клиентов (один раз для всех тестов)
        userAdminClient = new UserAdminClient();
        authClient = new AuthClient();
        accountClient = new AccountClient();

        // Инициализация сервиса
        userTestService = new UserTestService(userAdminClient);
    }
}