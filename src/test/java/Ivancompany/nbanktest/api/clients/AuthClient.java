package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.LoginRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public String loginAndGetToken(LoginRequest loginRequest) {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login") // путь к эндпоинту
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Берём токен из заголовка Authorization
        return response.getHeader("Authorization");
    }
}
