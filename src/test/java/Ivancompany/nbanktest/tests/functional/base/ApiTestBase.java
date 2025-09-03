package Ivancompany.nbanktest.tests.functional.base;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.clients.AuthClient;
import Ivancompany.nbanktest.api.clients.UserAdminClient;
import Ivancompany.nbanktest.core.config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

public class ApiTestBase {
    protected UserAdminClient userAdminClient;
    protected AuthClient authClient;
    protected AccountClient accountClient;

    @BeforeEach
    void setUpApi() {
        // Устанавливаем базовый URL для RestAssured
        RestAssured.baseURI = TestConfig.BASE_URL;

        // Подключаем Allure фильтр для логирования запросов и ответов
        RestAssured.filters(new AllureRestAssured());

        // Инициализация клиентов
        userAdminClient = new UserAdminClient();
        authClient = new AuthClient();
        accountClient = new AccountClient();
    }
}