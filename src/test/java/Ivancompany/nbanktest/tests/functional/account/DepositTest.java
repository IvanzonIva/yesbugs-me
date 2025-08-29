package Ivancompany.nbanktest.tests.functional.account;

import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.api.dto.response.AccountResponse;
import Ivancompany.nbanktest.tests.functional.base.BaseTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DepositTest extends BaseTest {

    @Test
    void shouldDepositMoneyToAccount() {
        // Позитивный сценарий: успешное пополнение
        Double depositAmount = 100.0;
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(depositAmount)
                .build();

        AccountResponse response = accountClient.deposit(userAuthHeader, depositRequest);

        assertThat(response.getId(), equalTo(accountId));
        assertThat(response.getBalance(), equalTo(depositAmount));
    }

    @Test
    void shouldFailDepositZeroAmount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(0.0)
                .build();

        var response = accountClient.depositRaw(userAuthHeader, depositRequest);
        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid account or amount"));
    }

    @Test
    void shouldFailDepositNegativeAmount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(-100.0)
                .build();

        var response = accountClient.depositRaw(userAuthHeader, depositRequest);
        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid account or amount"));
    }

    @Test
    void shouldFailDepositOverMaxAmount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(5001.0)
                .build();

        var response = accountClient.depositRaw(userAuthHeader, depositRequest);
        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Deposit amount exceeds the 5000 limit"));
    }

    @Test
    void shouldFailDepositWithoutAuthorization() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(accountId)
                .balance(100.0)
                .build();

        var response = accountClient.depositRaw(null, depositRequest);
        assertThat(response.getStatusCode(), equalTo(401));
        assertThat(response.getBody().asString(), equalTo(""));
    }

    @Test
    void shouldFailDepositToNonexistentAccount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(9999L) // явно несуществующий аккаунт
                .balance(100.0)
                .build();

        var response = accountClient.depositRaw(userAuthHeader, depositRequest);
        assertThat(response.getStatusCode(), equalTo(403));
        assertThat(response.getBody().asString(), equalTo("Unauthorized access to account"));
    }

    @Test
    void shouldFailDepositToOtherUserAccount() {
        DepositRequest depositRequest = DepositRequest.builder()
                .id(99L) // чужой аккаунт
                .balance(100.0)
                .build();

        var response = accountClient.depositRaw(userAuthHeader, depositRequest);
        assertThat(response.getStatusCode(), equalTo(403));
        assertThat(response.getBody().asString(), equalTo("Unauthorized access to account"));
    }
}
