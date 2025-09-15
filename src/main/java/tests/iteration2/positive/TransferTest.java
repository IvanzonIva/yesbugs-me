package tests.iteration2.positive;

import io.restassured.response.ValidatableResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.TransferRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import tests.iteration1.BaseTest;
import utils.AccountBalanceUtils;
import utils.TestDataFactory;

public class TransferTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {100.0, 1.0, 10000.0})
    public void authUserCanTransferMoneyToAnotherOwnAccount(double transferAmount) {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        // кладём денег чуть больше, чем собираемся переводить
        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdOne, transferAmount * 2);
        UserSteps.Deposit(createdUser, makeDeposit);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountIdOne);

        TransferRequest transferRequestModel =
                TestDataFactory.createTransferModel(accountIdOne, accountIdTwo, transferAmount);
        UserSteps.makeTransfer(createdUser, transferRequestModel);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountIdOne);

        softly.assertThat(senderAccountBalanceBefore)
                .isEqualTo(senderAccountBalanceAfter + transferAmount);
    }

    @ParameterizedTest
    @ValueSource(doubles = {100.0, 1.0, 10000.0})
    public void authUserCanTransferMoneyToAnotherUserAccount(double transferAmount) {

        CreateUserRequest createdUser1 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser1);

        CreateUserRequest createdUser2 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser2);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser1);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser2);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdOne, transferAmount * 2);
        UserSteps.Deposit(createdUser1, makeDeposit);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser1.getUsername(), createdUser1.getPassword(), accountIdOne);
        double receiverAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser2.getUsername(), createdUser2.getPassword(), accountIdTwo);

        TransferRequest transferRequestModel =
                TestDataFactory.createTransferModel(accountIdOne, accountIdTwo, transferAmount);
        UserSteps.makeTransfer(createdUser1, transferRequestModel);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser1.getUsername(), createdUser1.getPassword(), accountIdOne);
        double receiverAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser2.getUsername(), createdUser2.getPassword(), accountIdTwo);

        softly.assertThat(senderAccountBalanceBefore)
                .isEqualTo(senderAccountBalanceAfter + transferAmount);
        softly.assertThat(receiverAccountBalanceBefore)
                .isEqualTo(receiverAccountBalanceAfter - transferAmount);
    }
}
