package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import io.restassured.response.Response;
import Ivancompany.nbanktest.core.config.TestConfig;

import java.util.Arrays;

public class UserAdminClient extends BaseClient {

    public UserResponse createUser(CreateUserRequest userRequest) {
        Response response = request(TestConfig.ADMIN_AUTH)
                .body(userRequest)
                .when()
                .post("/admin/users")
                .then()
                .extract()
                .response();

        if (response.statusCode() != 201) {
            throw new RuntimeException("Не удалось создать пользователя. Статус: "
                    + response.statusCode() + ", ответ: " + response.asString());
        }

        return response.as(UserResponse.class);
    }

    public void deleteUser(Long userId) {
        Response response = request(TestConfig.ADMIN_AUTH)
                .when()
                .delete("/admin/users/" + userId)
                .then()
                .extract()
                .response();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удалось удалить пользователя. Статус: "
                    + response.statusCode() + ", ответ: " + response.asString());
        }
    }

    public UserResponse[] getAllUsers() {
        Response response = request(TestConfig.ADMIN_AUTH)
                .when()
                .get("/admin/users")
                .then()
                .extract()
                .response();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удалось получить пользователей. Статус: "
                    + response.statusCode() + ", ответ: " + response.asString());
        }

        return response.as(UserResponse[].class);
    }

    public UserResponse getUserById(Long userId) {
        return Arrays.stream(getAllUsers())
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + userId + " не найден"));
    }

    /**
     * Возвращает баланс конкретного аккаунта пользователя.
     */
    public double getAccountBalance(Long userId, Long accountId) {
        UserResponse user = getUserById(userId);

        return user.getAccounts().stream()
                .filter(acc -> acc.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Аккаунт с id " + accountId + " не найден у пользователя " + userId))
                .getBalance();
    }
}
