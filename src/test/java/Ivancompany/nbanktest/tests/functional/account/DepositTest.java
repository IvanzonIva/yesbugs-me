package Ivancompany.nbanktest.tests.functional.account;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.core.models.Role;
import Ivancompany.nbanktest.core.services.UserTestService;
import Ivancompany.nbanktest.core.steps.AccountSteps;
import Ivancompany.nbanktest.core.utils.DataGenerator;
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

public class DepositTest extends ApiTestBase {

    private static final double MAX_DEPOSIT_LIMIT = 5000.0;
    private static final double ZERO_AMOUNT = 0.0;
    private static final double NEGATIVE_AMOUNT = -100.0;
    private static final double OVER_LIMIT_AMOUNT = 5001.0;

    // Константы для сообщений об ошибках
    private static final String INVALID_ACCOUNT_OR_AMOUNT_MESSAGE = "Invalid account or amount";
    private static final String DEPOSIT_AMOUNT_EXCEEDS_LIMIT_MESSAGE = "Deposit amount exceeds the 5000 limit";

    private final AccountClient accountClient = new AccountClient();
    private final AccountSteps accountSteps = new AccountSteps();
    private final UserTestService userTestService = new UserTestService(userAdminClient);

    private UserTestHelper.UserTestData createdUser;

    @AfterEach
    void tearDown() {
        if (createdUser != null) {
            userTestService.safelyDeleteUser(createdUser.userId());
        }
    }

    @Test
    void userCanDepositMoney() {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);

        // Проверяем начальный баланс через админский клиент
        double initialBalance = userAdminClient.getAccountBalance(
                createdUser.userId(),
                createdUser.accountId()
        );

        double depositAmount = DataGenerator.generateAmount();

        // Пополняем счет пользователя через steps
        accountSteps.deposit(createdUser.authHeader(), createdUser.accountId(), depositAmount);

        // Проверяем баланс после депозита через админский клиент
        double finalBalance = userAdminClient.getAccountBalance(
                createdUser.userId(),
                createdUser.accountId()
        );

        assertThat(finalBalance, equalTo(initialBalance + depositAmount));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDepositAmounts")
    void userCannotDepositInvalidAmounts(double depositAmount, int expectedStatus, String expectedMessage) {
        createdUser = UserTestHelper.createUserWithAccount(Role.USER);

        // Используем AccountClient напрямую для получения сырого ответа
        DepositRequest depositRequest = DepositRequest.builder()
                .id(createdUser.accountId())
                .balance(depositAmount)
                .build();

        Response response = accountClient.depositRaw(createdUser.authHeader(), depositRequest);

        assertThat(response.getStatusCode(), equalTo(expectedStatus));
        assertThat(response.getBody().asString(), equalTo(expectedMessage));
    }

    private static Stream<Arguments> provideInvalidDepositAmounts() {
        return Stream.of(
                Arguments.of(ZERO_AMOUNT, HttpStatus.SC_BAD_REQUEST, INVALID_ACCOUNT_OR_AMOUNT_MESSAGE),
                Arguments.of(NEGATIVE_AMOUNT, HttpStatus.SC_BAD_REQUEST, INVALID_ACCOUNT_OR_AMOUNT_MESSAGE),
                Arguments.of(OVER_LIMIT_AMOUNT, HttpStatus.SC_BAD_REQUEST, DEPOSIT_AMOUNT_EXCEEDS_LIMIT_MESSAGE)
        );
    }
}