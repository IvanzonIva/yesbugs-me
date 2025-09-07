package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.specs.RequestSpecs;
import Ivancompany.nbanktest.core.specs.ResponseSpecs;
import io.restassured.response.Response;

import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class UserAdminClient {

    public UserResponse createUser(CreateUserRequest userRequest) {
        return given()
                .spec(RequestSpecs.adminSpec())
                .body(userRequest)
                .when()
                .post("/admin/users")
                .then()
                .spec(ResponseSpecs.created())
                .extract()
                .as(UserResponse.class);
    }

    public Response createUserRaw(CreateUserRequest userRequest) {
        return given()
                .spec(RequestSpecs.adminSpec())
                .body(userRequest)
                .when()
                .post("/admin/users");
    }

    public void deleteUser(Long userId) {
        given()
                .spec(RequestSpecs.adminSpec())
                .when()
                .delete("/admin/users/" + userId)
                .then()
                .spec(ResponseSpecs.ok());
    }

    public Response deleteUserRaw(Long userId) {
        return given()
                .spec(RequestSpecs.adminSpec())
                .when()
                .delete("/admin/users/" + userId);
    }

    public UserResponse[] getAllUsers() {
        return given()
                .spec(RequestSpecs.adminSpec())
                .when()
                .get("/admin/users")
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .as(UserResponse[].class);
    }

    public Response getAllUsersRaw() {
        return given()
                .spec(RequestSpecs.adminSpec())
                .when()
                .get("/admin/users");
    }

    // Вспомогательные методы
    public UserResponse getUserById(Long userId) {
        return Arrays.stream(getAllUsers())
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Пользователь с id %d не найден", userId)
                ));
    }

    public double getAccountBalance(Long userId, Long accountId) {
        UserResponse user = getUserById(userId);

        return user.getAccounts().stream()
                .filter(acc -> acc.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Аккаунт с id %d не найден у пользователя %d", accountId, userId)
                ))
                .getBalance();
    }

    // Новый метод для получения профиля пользователя по ID
    public UserResponse getProfile(Long userId) {
        return Arrays.stream(getAllUsers())
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Пользователь с id %d не найден", userId)
                ));
    }
}