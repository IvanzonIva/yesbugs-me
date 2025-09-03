package Ivancompany.nbanktest.tests.functional.account;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.clients.UserAdminClient;
import Ivancompany.nbanktest.api.dto.request.DepositRequest;
import Ivancompany.nbanktest.core.utils.DataGenerator;
import Ivancompany.nbanktest.core.utils.UserTestHelper;
import Ivancompany.nbanktest.tests.functional.base.ApiTestBase;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DepositTest extends ApiTestBase {

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
    void userCanDepositMoney() {
        createdUser = UserTestHelper.createUserWithAccount("USER");

        // Проверяем начальный баланс через админский клиент
        double initialBalance = userAdminClient.getAccountBalance(
                createdUser.userId(),
                createdUser.accountId()
        );

        double depositAmount = DataGenerator.generateAmount();

        DepositRequest request = DepositRequest.builder()
                .id(createdUser.accountId())
                .balance(depositAmount)
                .build();

        // Пополняем счет пользователя
        accountClient.deposit(createdUser.authHeader(), request);

        // Проверяем баланс после депозита через админский клиент
        double finalBalance = userAdminClient.getAccountBalance(
                createdUser.userId(),
                createdUser.accountId()
        );

        assertThat(finalBalance, equalTo(initialBalance + depositAmount));
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, 400, Invalid account or amount",
            "-100.0, 400, Invalid account or amount",
            "5001.0, 400, Deposit amount exceeds the 5000 limit"
    })
    void userCannotDepositInvalidAmounts(double depositAmount, int expectedStatus, String expectedMessage) {
        createdUser = UserTestHelper.createUserWithAccount("USER");

        DepositRequest request = DepositRequest.builder()
                .id(createdUser.accountId())
                .balance(depositAmount)
                .build();

        Response response = accountClient.depositRaw(createdUser.authHeader(), request);

        assertThat(response.getStatusCode(), equalTo(expectedStatus));
        assertThat(response.getBody().asString(), equalTo(expectedMessage));
    }
}