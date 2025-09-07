package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.LoginRequest;
import Ivancompany.nbanktest.core.specs.ResponseSpecs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public String loginAndGetToken(LoginRequest loginRequest) {
        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .response();

        return response.getHeader("Authorization");
    }

    public Response loginRaw(LoginRequest loginRequest) {
        return given()
                .header("Content-Type", "application/json")
                .body(loginRequest)
                .when()
                .post("/auth/login");
    }
}