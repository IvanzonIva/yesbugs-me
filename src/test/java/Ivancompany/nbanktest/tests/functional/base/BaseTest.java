package Ivancompany.nbanktest.tests.functional.base;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.clients.AuthClient;
import Ivancompany.nbanktest.api.clients.UserAdminClient;
import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.config.TestConfig;
import Ivancompany.nbanktest.core.utils.DataGenerator;
import Ivancompany.nbanktest.core.utils.AuthHelper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    protected UserAdminClient userAdminClient;
    protected AuthClient authClient;
    protected AccountClient accountClient;

    protected String username;
    protected String password;
    protected String userAuthHeader;
    protected Long userId;
    protected Long accountId;

    @BeforeEach
    void setUp() {
        // Устанавливаем базовый URL для всех RestAssured запросов
        RestAssured.baseURI = TestConfig.BASE_URL;

        // Добавляем фильтр для логирования в Allure
        RestAssured.filters(new AllureRestAssured());

        userAdminClient = new UserAdminClient();
        authClient = new AuthClient();
        accountClient = new AccountClient();

        // Генерация случайного username и password
        username = DataGenerator.generateValidUsername();
        password = DataGenerator.generateValidPassword();

        // Создание пользователя через AdminClient
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role("USER")
                .build();

        UserResponse userResponse = userAdminClient.createUser(userRequest);
        userId = userResponse.getId();

        // Генерация Basic Auth Header для пользователя
        userAuthHeader = AuthHelper.generateBasicAuthHeader(username, password);

        // Создание аккаунта для пользователя
        var accountResponse = accountClient.createAccount(userAuthHeader);
        accountId = accountResponse.getId();
    }

    @AfterEach
    void tearDown() {
        if (userId != null) {
            userAdminClient.deleteUser(userId);
        }
    }
}
