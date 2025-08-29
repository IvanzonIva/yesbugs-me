package Ivancompany.nbanktest.tests.functional.account;

import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.api.dto.response.AccountResponse;
import Ivancompany.nbanktest.tests.functional.base.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TransferTest extends BaseTest {

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        // Создаем второй аккаунт для пользователя
        AccountResponse account2 = accountClient.createAccount(userAuthHeader);

        // Депозит на первый аккаунт
        double initialDeposit = 200.0;
        accountClient.deposit(userAuthHeader, DepositRequest.builder()
                .id(accountId)
                .balance(initialDeposit)
                .build());

        // Выполняем перевод
        double transferAmount = 50.0;
        accountClient.transfer(userAuthHeader, accountId, account2.getId(), transferAmount);

        // Проверяем балансы после перевода
        AccountResponse updatedAccount1 = accountClient.getAccount(userAuthHeader, accountId);
        AccountResponse updatedAccount2 = accountClient.getAccount(userAuthHeader, account2.getId());

        assertThat(updatedAccount1.getBalance(), equalTo(initialDeposit - transferAmount));
        assertThat(updatedAccount2.getBalance(), equalTo(transferAmount));
    }

    @Test
    void shouldFailTransferOverMaxAmount() {
        AccountResponse account2 = accountClient.createAccount(userAuthHeader);

        double transferAmount = 10001.0; // больше лимита
        Response response = accountClient.transferRaw(userAuthHeader, accountId, account2.getId(), transferAmount);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Transfer amount cannot exceed 10000"));
    }

    @Test
    void shouldFailTransferZeroAmount() {
        AccountResponse account2 = accountClient.createAccount(userAuthHeader);

        double transferAmount = 0.0;
        Response response = accountClient.transferRaw(userAuthHeader, accountId, account2.getId(), transferAmount);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    void shouldFailTransferNegativeAmount() {
        AccountResponse account2 = accountClient.createAccount(userAuthHeader);

        double transferAmount = -50.0;
        Response response = accountClient.transferRaw(userAuthHeader, accountId, account2.getId(), transferAmount);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    void shouldFailTransferToSameAccount() {
        Response response = accountClient.transferRaw(userAuthHeader, accountId, accountId, 100.0);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    void shouldFailTransferWithoutAuthorization() {
        AccountResponse account2 = accountClient.createAccount(userAuthHeader);

        Response response = accountClient.transferRaw(null, accountId, account2.getId(), 50.0);

        assertThat(response.getStatusCode(), equalTo(401));
        assertThat(response.getBody().asString(), equalTo(""));
    }

    @Test
    void shouldFailTransferToNonexistentAccount() {
        Response response = accountClient.transferRaw(userAuthHeader, accountId, 9999L, 50.0);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    void shouldFailTransferFromNonexistentAccount() {
        AccountResponse account2 = accountClient.createAccount(userAuthHeader);

        Response response = accountClient.transferRaw(userAuthHeader, 9999L, account2.getId(), 50.0);

        assertThat(response.getStatusCode(), equalTo(403));
        assertThat(response.getBody().asString(), equalTo("Unauthorized access to account"));
    }
}
