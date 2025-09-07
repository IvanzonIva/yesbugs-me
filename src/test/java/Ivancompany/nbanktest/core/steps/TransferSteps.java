package Ivancompany.nbanktest.core.steps;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.dto.response.TransferResponse;
import io.restassured.response.Response;

public class TransferSteps {
    private final AccountClient accountClient = new AccountClient();

    public TransferResponse transfer(String authHeader, Long senderId, Long receiverId, Double amount) {
        return accountClient.transfer(authHeader, senderId, receiverId, amount);
    }

    public Response transferRaw(String authHeader, Long senderId, Long receiverId, Double amount) {
        return accountClient.transferRaw(authHeader, senderId, receiverId, amount);
    }
}