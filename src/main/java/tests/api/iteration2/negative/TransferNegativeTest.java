package tests.api.iteration2.negative;

import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.TransferRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import tests.api.iteration1.BaseTest;
import api.utils.AccountBalanceUtils;
import api.utils.TestDataFactory;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.UserSteps;

public class TransferNegativeTest extends BaseTest {
    private static final long NOT_EXISTING_ACCOUNT_ID = 123456;
    private static final double DEPOSIT_AMOUNT = 100.00;
    private static final double AMOUNT_MORE_THAN_ACCOUNT_HAS = 4000.00;
    private static final double NEGATIVE_AMOUNT = -66.00;



    @Test
    public void authUserCanNotTransferMoneyToOwnAccountMoreThatAccountHas() {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdOne, DEPOSIT_AMOUNT);
        UserSteps.Deposit(createdUser, makeDeposit);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountIdOne);

        TransferRequest transferRequestModel = TestDataFactory.createTransferModel(accountIdOne, accountIdTwo, AMOUNT_MORE_THAN_ACCOUNT_HAS);

        new CrudRequesters(
                RequestSpecs.authAsUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferRequestModel);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountIdOne);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }

    @Test
    public void authUserCanNotTransferMoneyToOwnAccountNegativeAmount() {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdOne, DEPOSIT_AMOUNT);

        DepositResponse responseModel = UserSteps.Deposit(createdUser, makeDeposit);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountIdOne);

        TransferRequest transferRequestModel = TestDataFactory.createTransferModel(accountIdOne, accountIdTwo, NEGATIVE_AMOUNT);

        new CrudRequesters(
                RequestSpecs.authAsUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferRequestModel);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountIdOne);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }

    @Test
    public void authUserCanNotTransferMoneyToNotExistingAccount() {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdOne, DEPOSIT_AMOUNT);
        UserSteps.Deposit(createdUser, makeDeposit);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountIdOne);

        TransferRequest transferRequestModel = TestDataFactory.createTransferModel(accountIdOne, NOT_EXISTING_ACCOUNT_ID, DEPOSIT_AMOUNT);

        new CrudRequesters(
                RequestSpecs.authAsUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadRequest("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferRequestModel);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser.getUsername(),
                createdUser.getPassword(), accountIdOne);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
    }

    @Test
    public void transferFromOtherAccountShouldBeForbidden() {

        CreateUserRequest createdUser1 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser1);

        CreateUserRequest createdUser2 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser2);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser1);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser2);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountIdOne, DEPOSIT_AMOUNT);

        DepositResponse responseModel = UserSteps.Deposit(createdUser1, makeDeposit);

        TransferRequest transferRequestModel = TestDataFactory.createTransferModel(accountIdTwo, accountIdOne, DEPOSIT_AMOUNT);

        double senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser1.getUsername(),
                createdUser1.getPassword(), accountIdOne);

        double receiverAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(createdUser2.getUsername(),
                createdUser2.getPassword(), accountIdTwo);

        new CrudRequesters(
                RequestSpecs.authAsUser(createdUser1.getUsername(), createdUser1.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsForbidden("Unauthorized access to account"))
                .post(transferRequestModel);

        double senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser1.getUsername(),
                createdUser1.getPassword(), accountIdOne);

        double receiverAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(createdUser2.getUsername(),
                createdUser2.getPassword(), accountIdTwo);

        softly.assertThat(senderAccountBalanceBefore).isEqualTo(senderAccountBalanceAfter);
        softly.assertThat(receiverAccountBalanceBefore).isEqualTo(receiverAccountBalanceAfter);
    }
}