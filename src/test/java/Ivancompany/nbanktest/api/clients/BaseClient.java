package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.core.specs.RequestSpecs;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    protected RequestSpecification request(String authHeader) {
        return given()
                .spec(RequestSpecs.authSpec(authHeader));
    }

    protected RequestSpecification requestWithoutAuth() {
        return given()
                .spec(RequestSpecs.unauthSpec());
    }
}