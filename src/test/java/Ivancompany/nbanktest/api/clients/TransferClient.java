package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.TransferRequest;
import Ivancompany.nbanktest.api.dto.response.TransferResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TransferClient {

    public TransferResponse transfer(String authHeader, TransferRequest transferRequest) {
        return given()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post("/accounts/transfer")
                .then()
                .statusCode(200)
                .extract()
                .as(TransferResponse.class);
    }
}