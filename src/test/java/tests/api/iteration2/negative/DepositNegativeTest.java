package tests.api.iteration2.negative;

import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.requests.skelethon.ErrorMessages;
import api.requests.steps.AdminSteps;
import api.specs.ResponseSpecs;
import tests.api.iteration1.BaseTest;
import api.utils.AccountBalanceUtils;
import api.utils.TestDataFactory;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.UserSteps;

import java.math.BigDecimal;

public class DepositNegativeTest extends BaseTest {
    private static final long NOT_EXISTING_ACCOUNT_ID = 6969;
    public static final BigDecimal DEPOSIT_AMOUNT = new BigDecimal("100.00");
    public static final BigDecimal NEGATIVE_DEPOSIT_AMOUNT = new BigDecimal("-99.00");
    public static final BigDecimal ZERO_DEPOSIT_AMOUNT = BigDecimal.ZERO;

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

        BigDecimal senderBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser1.getUsername(), createdUser1.getPassword(), accountIdOne);

        UserSteps.Deposit(createdUser1, makeDeposit,
                ResponseSpecs.requestReturnsForbidden(ErrorMessages.UNAUTHORIZED_ACCESS.getMessage()));

        BigDecimal senderBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser1.getUsername(), createdUser1.getPassword(), accountIdOne);

        softly.assertThat(senderBalanceBefore).isEqualByComparingTo(senderBalanceAfter);
    }

    @Test
    public void userCannotDepositToNotExistingUserAccountId() {
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(NOT_EXISTING_ACCOUNT_ID, DEPOSIT_AMOUNT);

        BigDecimal balanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountId);

        UserSteps.Deposit(createdUser, makeDeposit,
                ResponseSpecs.requestReturnsForbidden(ErrorMessages.UNAUTHORIZED_ACCESS.getMessage()));

        BigDecimal balanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountId);

        softly.assertThat(balanceBefore).isEqualByComparingTo(balanceAfter);
    }

    @Test
    public void userCannotMakeNegativeDeposit() {
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountId, NEGATIVE_DEPOSIT_AMOUNT);

        BigDecimal balanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountId);

        UserSteps.Deposit(createdUser, makeDeposit,
                ResponseSpecs.requestReturnsBadRequest(ErrorMessages.INVALID_ACCOUNT_OR_AMOUNT.getMessage()));

        BigDecimal balanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountId);

        softly.assertThat(balanceBefore).isEqualByComparingTo(balanceAfter);
    }

    @Test
    public void userCannotMakeZeroDeposit() {
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        DepositRequest makeDeposit = TestDataFactory.createDepositModel(accountId, ZERO_DEPOSIT_AMOUNT);

        BigDecimal balanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountId);

        UserSteps.Deposit(createdUser, makeDeposit,
                ResponseSpecs.requestReturnsBadRequest(ErrorMessages.INVALID_ACCOUNT_OR_AMOUNT.getMessage()));

        BigDecimal balanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountId);

        softly.assertThat(balanceBefore).isEqualByComparingTo(balanceAfter);
    }
}