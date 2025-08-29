package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.config.TestConfig;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserAdminClient {

    public UserResponse createUser(CreateUserRequest userRequest) {
        Response response = given()
                .baseUri(TestConfig.BASE_URL)
                .header("Authorization", TestConfig.ADMIN_AUTH)
                .header("Content-Type", "application/json")
                .body(userRequest)
                .when()
                .post("/admin/users")
                .then()
                .log().all()
                .extract()
                .response();

        if (response.statusCode() != 201) {
            throw new RuntimeException("Не удалось создать пользователя. Статус: "
                    + response.statusCode() + ", ответ: " + response.asString());
        }

        return response.as(UserResponse.class);
    }

    public Response deleteUser(Long userId) {
        Response response = given()
                .baseUri(TestConfig.BASE_URL)
                .header("Authorization", TestConfig.ADMIN_AUTH)
                .when()
                .delete("/admin/users/" + userId)
                .then()
                .log().all()
                .extract()
                .response();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удалось удалить пользователя. Статус: "
                    + response.statusCode() + ", ответ: " + response.asString());
        }

        return response;
    }
}
