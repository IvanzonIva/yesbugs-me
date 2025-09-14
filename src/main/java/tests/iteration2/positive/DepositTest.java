package tests.iteration2.positive;

import io.restassured.response.ValidatableResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.DepositResponse;
import org.junit.jupiter.api.Test;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import tests.iteration1.BaseTest;
import utils.AccountBalanceUtils;
import utils.TestDataFactory;

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

        softly.assertThat(makeDeposit.getBalance()).isEqualTo(responseModel.getBalance());
        softly.assertThat(makeDeposit.getId()).isEqualTo(responseModel.getId());
        softly.assertThat(accountBalanceBefore).isEqualTo(accountBalanceAfter - DEPOSIT_AMOUNT);
    }

}
