package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.api.dto.request.TransferRequest;
import Ivancompany.nbanktest.api.dto.response.AccountResponse;
import Ivancompany.nbanktest.api.dto.response.TransferResponse;
import Ivancompany.nbanktest.core.specs.ResponseSpecs;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AccountClient {

    public AccountResponse createAccount(String authHeader) {
        return given()
                .header("Authorization", authHeader)
                .when()
                .post("/accounts")
                .then()
                .spec(ResponseSpecs.created())
                .extract()
                .as(AccountResponse.class);
    }

    public Response createAccountRaw(String authHeader) {
        var request = given();

        if (authHeader != null) {
            request.header("Authorization", authHeader);
        }

        return request
                .when()
                .post("/accounts");
    }

    public AccountResponse deposit(String authHeader, DepositRequest depositRequest) {
        return given()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .body(depositRequest)
                .when()
                .post("/accounts/deposit")
                .then()
                .spec(ResponseSpecs.ok())
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
                .spec(ResponseSpecs.ok())
                .extract()
                .as(TransferResponse.class);
    }

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