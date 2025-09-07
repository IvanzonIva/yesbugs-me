package Ivancompany.nbanktest.core.steps;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.api.dto.response.AccountResponse;

public class AccountSteps {
    private final AccountClient accountClient = new AccountClient();

    public AccountResponse createAccount(String authHeader) {
        return accountClient.createAccount(authHeader);
    }

    public void deposit(String authHeader, Long accountId, double amount) {
        accountClient.deposit(authHeader, DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build());
    }

}