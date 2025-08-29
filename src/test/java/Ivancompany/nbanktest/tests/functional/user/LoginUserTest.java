package Ivancompany.nbanktest.tests.functional.user;

import Ivancompany.nbanktest.core.utils.DataGenerator; // <- добавь этот импорт
import Ivancompany.nbanktest.api.dto.request.LoginRequest;
import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.tests.functional.base.BaseTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest extends BaseTest {

    @Test
    void adminCanGenerateAuthTokenTest() {
        // Создаём нового пользователя
        String username = DataGenerator.generateValidUsername();
        String password = DataGenerator.generateValidPassword();

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role("USER")
                .build();

        UserResponse userResponse = userAdminClient.createUser(userRequest);
        Long createdUserId = userResponse.getId();

        try {
            // Логинимся как пользователь
            LoginRequest loginRequest = LoginRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            // Получаем токен из заголовка
            String token = authClient.loginAndGetToken(loginRequest);
            assertThat(token, notNullValue());

        } finally {
            // Удаляем пользователя после теста
            if (createdUserId != null) {
                userAdminClient.deleteUser(createdUserId);
            }
        }
    }
}
