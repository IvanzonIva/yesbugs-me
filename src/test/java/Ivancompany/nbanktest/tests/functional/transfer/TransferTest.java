package Ivancompany.nbanktest.tests.functional.account;

import Ivancompany.nbanktest.core.models.Role;
import Ivancompany.nbanktest.core.services.UserTestService;
import Ivancompany.nbanktest.core.steps.AccountSteps;
import Ivancompany.nbanktest.core.steps.TransferSteps;
import Ivancompany.nbanktest.core.utils.UserTestHelper;
import Ivancompany.nbanktest.tests.functional.base.ApiTestBase;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

    // Константы для сообщений об ошибках
    private static final String TRANSFER_AMOUNT_EXCEED_TEMPLATE = "Transfer amount cannot exceed %d";
    private static final String INVALID_TRANSFER_MESSAGE = "Invalid transfer: insufficient funds or invalid accounts";
    private static final String UNAUTHORIZED_ACCESS_MESSAGE = "Unauthorized access to account";
    private static final String EMPTY_RESPONSE_MESSAGE = "";

    private final AccountSteps accountSteps = new AccountSteps();
    private final TransferSteps transferSteps = new TransferSteps();
    private final UserTestService userTestService = new UserTestService(userAdminClient);

    private UserTestHelper.UserTestData createdUser;

    @AfterEach
    void tearDown() {
        if (createdUser != null) {
            userTestService.safelyDeleteUser(createdUser.userId());
        }
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);
        var additionalAccount = accountSteps.createAccount(createdUser.authHeader());

        accountSteps.deposit(createdUser.authHeader(), createdUser.accountId(), INITIAL_DEPOSIT);

        // Получаем балансы
        double balanceBefore1 = userAdminClient.getAccountBalance(createdUser.userId(), createdUser.accountId());
        double balanceBefore2 = userAdminClient.getAccountBalance(createdUser.userId(), additionalAccount.getId());

        transferSteps.transfer(createdUser.authHeader(), createdUser.accountId(), additionalAccount.getId(), VALID_TRANSFER_AMOUNT);

        // Получаем балансы после перевода
        double balanceAfter1 = userAdminClient.getAccountBalance(createdUser.userId(), createdUser.accountId());
        double balanceAfter2 = userAdminClient.getAccountBalance(createdUser.userId(), additionalAccount.getId());

        assertThat(balanceAfter1, equalTo(balanceBefore1 - VALID_TRANSFER_AMOUNT));
        assertThat(balanceAfter2, equalTo(balanceBefore2 + VALID_TRANSFER_AMOUNT));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTransfers")
    void shouldFailInvalidTransfers(double transferAmount, int expectedStatus, String expectedMessage) {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);
        var additionalAccount = accountSteps.createAccount(createdUser.authHeader());

        Response response = transferSteps.transferRaw(
                createdUser.authHeader(),
                createdUser.accountId(),
                additionalAccount.getId(),
                transferAmount
        );

        assertThat(response.getStatusCode(), equalTo(expectedStatus));
        assertThat(response.getBody().asString(), equalTo(expectedMessage));
    }

    private static Stream<Arguments> provideInvalidTransfers() {
        return Stream.of(
                Arguments.of(
                        OVER_MAX_TRANSFER,
                        HttpStatus.SC_BAD_REQUEST,
                        String.format(TRANSFER_AMOUNT_EXCEED_TEMPLATE, MAX_TRANSFER_LIMIT)
                ),
                Arguments.of(ZERO_TRANSFER, HttpStatus.SC_BAD_REQUEST, INVALID_TRANSFER_MESSAGE),
                Arguments.of(NEGATIVE_TRANSFER, HttpStatus.SC_BAD_REQUEST, INVALID_TRANSFER_MESSAGE)
        );
    }

    @Test
    void shouldFailTransferToSameAccount() {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);

        Response response = transferSteps.transferRaw(
                createdUser.authHeader(),
                createdUser.accountId(),
                createdUser.accountId(),
                VALID_TRANSFER_AMOUNT
        );

        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().asString(), equalTo(INVALID_TRANSFER_MESSAGE));
    }

    @Test
    void shouldFailTransferWithoutAuthorization() {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);
        var additionalAccount = accountSteps.createAccount(createdUser.authHeader());

        Response response = transferSteps.transferRaw(
                null,
                createdUser.accountId(),
                additionalAccount.getId(),
                VALID_TRANSFER_AMOUNT
        );

        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat(response.getBody().asString(), equalTo(EMPTY_RESPONSE_MESSAGE));
    }

    @Test
    void shouldFailTransferToNonexistentAccount() {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);

        Response response = transferSteps.transferRaw(
                createdUser.authHeader(),
                createdUser.accountId(),
                NON_EXISTENT_ACCOUNT,
                VALID_TRANSFER_AMOUNT
        );

        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().asString(), equalTo(INVALID_TRANSFER_MESSAGE));
    }

    @Test
    void shouldFailTransferFromNonexistentAccount() {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);
        var additionalAccount = accountSteps.createAccount(createdUser.authHeader());

        Response response = transferSteps.transferRaw(
                createdUser.authHeader(),
                NON_EXISTENT_ACCOUNT,
                additionalAccount.getId(),
                VALID_TRANSFER_AMOUNT
        );

        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_FORBIDDEN));
        assertThat(response.getBody().asString(), equalTo(UNAUTHORIZED_ACCESS_MESSAGE));
    }
}