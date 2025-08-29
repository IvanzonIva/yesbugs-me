package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.api.dto.request.TransferRequest;
import Ivancompany.nbanktest.api.dto.response.AccountResponse;
import Ivancompany.nbanktest.api.dto.response.TransferResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AccountClient {

    public AccountResponse createAccount(String authHeader) {
        return given()
                .header("Authorization", authHeader)
                .when()
                .post("/accounts")
                .then()
                .statusCode(201)
                .extract()
                .as(AccountResponse.class);
    }

    public AccountResponse deposit(String authHeader, DepositRequest depositRequest) {
        return given()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .body(depositRequest)
                .when()
                .post("/accounts/deposit")
                .then()
                .statusCode(200)
                .extract()
                .as(AccountResponse.class);
    }

    public Response depositRaw(String authHeader, DepositRequest depositRequest) {
        var request = given()
                .header("Content-Type", "application/json")
                .body(depositRequest);

        if (authHeader != null) {
            request.header("Authorization", authHeader);
        }

        return request
                .when()
                .post("/accounts/deposit");
    }

    public AccountResponse getAccount(String authHeader, Long accountId) {
        return given()
                .header("Authorization", authHeader)
                .when()
                .get("/accounts/" + accountId)
                .then()
                .statusCode(200)
                .extract()
                .as(AccountResponse.class);
    }

    // Метод для успешного перевода с десериализацией TransferResponse
    public TransferResponse transfer(String authHeader, Long senderId, Long receiverId, Double amount) {
        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        return given()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("/accounts/transfer")
                .then()
                .statusCode(200)
                .extract()
                .as(TransferResponse.class);
    }

    // Метод для негативных сценариев — не проверяет статус
    public Response transferRaw(String authHeader, Long senderId, Long receiverId, Double amount) {
        TransferRequest request = TransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        var reqSpec = given()
                .header("Content-Type", "application/json")
                .body(request);

        if (authHeader != null) {
            reqSpec.header("Authorization", authHeader);
        }

        return reqSpec
                .when()
                .post("/accounts/transfer");
    }
}
