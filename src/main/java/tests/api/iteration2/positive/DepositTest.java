package tests.api.iteration2.positive;

import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import tests.api.iteration1.BaseTest;
import api.utils.AccountBalanceUtils;
import api.utils.TestDataFactory;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

public class DepositTest extends BaseTest {
    public static final double DEPOSIT_AMOUNT = 100.00;

    @Test
    public void authUserCanDepositMoneyWithValidAmount() {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountId, DEPOSIT_AMOUNT);

        double accountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountId);

        DepositResponse responseModel = UserSteps.Deposit(createdUser, makeDeposit);

        double accountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountId);
        // сравнение моделей вместо поштучных полей
        ModelAssertions.assertThatModels(makeDeposit, responseModel)
                .as("Поля запроса и ответа должны совпадать")
                .match();
        softly.assertThat(accountBalanceBefore).isEqualTo(accountBalanceAfter - DEPOSIT_AMOUNT);
    }

}