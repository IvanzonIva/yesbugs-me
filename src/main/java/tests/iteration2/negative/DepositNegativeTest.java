package tests.iteration2.negative;

import io.restassured.response.ValidatableResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequesters;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import tests.iteration1.BaseTest;
import utils.AccountBalanceUtils;
import utils.TestDataFactory;

public class DepositNegativeTest extends BaseTest {
    private static final long NOT_EXISTING_ACCOUNT_ID = 6969;
    public static final double DEPOSIT_AMOUNT = 100.00;
    public static final double NEGATIVE_DEPOSIT_AMOUNT = -99.00;
    public static final double ZERO_DEPOSIT_AMOUNT = 0.00;

    @Test
    public void userCannotDepositToAnotherUserAccount() {

        CreateUserRequest createdUser1 = TestDataFactory.createUserModel();
        CreateUserRequest createdUser2 = TestDataFactory.createUserModel();

        AdminSteps.createUser(createdUser1);
        AdminSteps.createUser(createdUser2);

        ValidatableResponse createAccountResponse1 = UserSteps.createAccount(createdUser1);
        long accountIdOne = UserSteps.getAccountID(createAccountResponse1);

        ValidatableResponse createAccountResponse2 = UserSteps.createAccount(createdUser2);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponse2);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdTwo, DEPOSIT_AMOUNT);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser1.getUsername(),
                createdUser1.getPassword(), accountIdOne);

        new CrudRequesters(RequestSpecs.depositAsAuthUser(
                createdUser1.getUsername(),
                createdUser1.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden("Unauthorized access to account"))
                .post(makeDeposit);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser1.getUsername(),
                createdUser1.getPassword(), accountIdOne);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }


    @Test
    public void userCannotDepositToNotExistingUserAccountId() {

        CreateUserRequest createdUser1 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser1);

        ValidatableResponse createAccountResponse1 = UserSteps.createAccount(createdUser1);
        long accountIdOne = UserSteps.getAccountID(createAccountResponse1);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(NOT_EXISTING_ACCOUNT_ID, DEPOSIT_AMOUNT);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser1.getUsername(),
                createdUser1.getPassword(), accountIdOne);

        new CrudRequesters(RequestSpecs.depositAsAuthUser(
                createdUser1.getUsername(),
                createdUser1.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsForbidden("Unauthorized access to account"))
                .post(makeDeposit);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser1.getUsername(),
                createdUser1.getPassword(), accountIdOne);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }


    @Test
    public void userCannotMakeNegativeDeposit() {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

       DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountId, NEGATIVE_DEPOSIT_AMOUNT);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountId);

        new CrudRequesters(RequestSpecs
                .depositAsAuthUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest("Invalid account or amount"))
                .post(makeDeposit);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountId);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }

    @Test
    public void userCannotMakeZeroDeposit() {
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountId, ZERO_DEPOSIT_AMOUNT);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountId);

        new CrudRequesters(RequestSpecs
                .depositAsAuthUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest("Invalid account or amount"))
                .post(makeDeposit);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountId);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }
}