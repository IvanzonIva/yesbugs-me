package Ivancompany.nbanktest.tests.functional.account;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.clients.UserAdminClient;
import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.api.dto.response.AccountResponse;
import Ivancompany.nbanktest.tests.functional.base.ApiTestBase;
import Ivancompany.nbanktest.core.utils.UserTestHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TransferTest extends ApiTestBase {

    private static final double INITIAL_DEPOSIT = 200.0;
    private static final double VALID_TRANSFER_AMOUNT = 50.0;
    private static final double OVER_MAX_TRANSFER = 10001.0;
    private static final double ZERO_TRANSFER = 0.0;
    private static final double NEGATIVE_TRANSFER = -50.0;
    private static final long NON_EXISTENT_ACCOUNT = 9999L;
    private static final int MAX_TRANSFER_LIMIT = 10000;

    private final AccountClient accountClient = new AccountClient();
    private final UserAdminClient userAdminClient = new UserAdminClient();

    private UserTestHelper.UserTestData createdUser;

    @AfterEach
    void tearDown() {
        if (createdUser != null) {
            userAdminClient.deleteUser(createdUser.userId());
        }
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        createdUser = UserTestHelper.createUserWithAccount("USER");
        var account2 = createSecondAccount();

        // Депозит на первый аккаунт
        deposit(createdUser.accountId(), INITIAL_DEPOSIT);

        // Балансы до перевода
        double balanceBefore1 = getBalance(createdUser.accountId());
        double balanceBefore2 = getBalance(account2.getId());

        // Перевод
        accountClient.transfer(createdUser.authHeader(), createdUser.accountId(), account2.getId(), VALID_TRANSFER_AMOUNT);

        // Балансы после перевода
        double balanceAfter1 = getBalance(createdUser.accountId());
        double balanceAfter2 = getBalance(account2.getId());

        assertThat(balanceAfter1, equalTo(balanceBefore1 - VALID_TRANSFER_AMOUNT));
        assertThat(balanceAfter2, equalTo(balanceBefore2 + VALID_TRANSFER_AMOUNT));
    }

    @ParameterizedTest(name = "Transfer {2} → ожидаем {1} и сообщение \"{3}\"")
    @CsvSource({
            "10001.0, 400, over max limit, Transfer amount cannot exceed " + MAX_TRANSFER_LIMIT,
            "0.0, 400, zero amount, Invalid transfer: insufficient funds or invalid accounts",
            "-50.0, 400, negative amount, Invalid transfer: insufficient funds or invalid accounts"
    })
    void shouldFailInvalidTransfers(double transferAmount, int expectedStatus, String caseName, String expectedMessage) {
        createdUser = UserTestHelper.createUserWithAccount("USER");
        var account2 = createSecondAccount();

        Response response = accountClient.transferRaw(createdUser.authHeader(), createdUser.accountId(), account2.getId(), transferAmount);

        assertThat(response.getStatusCode(), equalTo(expectedStatus));
        assertThat(response.getBody().asString(), equalTo(expectedMessage));
    }

    @Test
    void shouldFailTransferToSameAccount() {
        createdUser = UserTestHelper.createUserWithAccount("USER");

        Response response = accountClient.transferRaw(createdUser.authHeader(), createdUser.accountId(), createdUser.accountId(), VALID_TRANSFER_AMOUNT);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    void shouldFailTransferWithoutAuthorization() {
        createdUser = UserTestHelper.createUserWithAccount("USER");
        var account2 = createSecondAccount();

        Response response = accountClient.transferRaw(null, createdUser.accountId(), account2.getId(), VALID_TRANSFER_AMOUNT);

        assertThat(response.getStatusCode(), equalTo(401));
        assertThat(response.getBody().asString(), equalTo(""));
    }

    @Test
    void shouldFailTransferToNonexistentAccount() {
        createdUser = UserTestHelper.createUserWithAccount("USER");

        Response response = accountClient.transferRaw(createdUser.authHeader(), createdUser.accountId(), NON_EXISTENT_ACCOUNT, VALID_TRANSFER_AMOUNT);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    void shouldFailTransferFromNonexistentAccount() {
        createdUser = UserTestHelper.createUserWithAccount("USER");
        var account2 = createSecondAccount();

        Response response = accountClient.transferRaw(createdUser.authHeader(), NON_EXISTENT_ACCOUNT, account2.getId(), VALID_TRANSFER_AMOUNT);

        assertThat(response.getStatusCode(), equalTo(403));
        assertThat(response.getBody().asString(), equalTo("Unauthorized access to account"));
    }

    // Хелперы
    private AccountResponse createSecondAccount() {
        return accountClient.createAccount(createdUser.authHeader());
    }

    private void deposit(Long accountId, double amount) {
        accountClient.deposit(createdUser.authHeader(), DepositRequest.builder()
                .id(accountId)
                .balance(amount)
                .build());
    }

    private double getBalance(Long accountId) {
        return accountClient.getAccount(createdUser.authHeader(), accountId).getBalance();
    }
}