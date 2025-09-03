package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.core.config.TestConfig;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    protected RequestSpecification request(String authHeader) {
        return given()
                .baseUri(TestConfig.BASE_URL)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json");
    }

    protected RequestSpecification requestWithoutAuth() {
        return given()
                .baseUri(TestConfig.BASE_URL)
                .header("Content-Type", "application/json");
    }
}
