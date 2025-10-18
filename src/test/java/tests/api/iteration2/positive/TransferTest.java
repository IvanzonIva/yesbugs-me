package tests.api.iteration2.positive;

import api.models.CreateUserRequest;
import api.models.TransferRequest;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import tests.api.iteration1.BaseTest;
import api.utils.AccountBalanceUtils;
import api.utils.TestDataFactory;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class TransferTest extends BaseTest {

    private static Stream<Arguments> transferAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("100.00")),
                Arguments.of(new BigDecimal("1.00")),
                Arguments.of(new BigDecimal("10000.00"))
        );
    }

    @ParameterizedTest
    @MethodSource("transferAmounts")
    @DisplayName("Авторизованный пользователь может переводить деньги между своими счетами")
    public void authUserCanTransferMoneyToAnotherOwnAccount(BigDecimal transferAmount) {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        // кладём денег чуть больше, чем собираемся переводить
        BigDecimal depositAmount = transferAmount.multiply(new BigDecimal("2"));
        AccountBalanceUtils.depositEnough(createdUser, accountIdOne, depositAmount);

        BigDecimal senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountIdOne);

        TransferRequest transferRequestModel =
                TestDataFactory.createTransferModel(accountIdOne, accountIdTwo, transferAmount);
        UserSteps.makeTransfer(createdUser, transferRequestModel);

        BigDecimal senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(), createdUser.getPassword(), accountIdOne);

        BigDecimal expectedBalance = senderAccountBalanceBefore.subtract(transferAmount);

        softly.assertThat(senderAccountBalanceAfter)
                .isEqualByComparingTo(expectedBalance);
    }

    @ParameterizedTest
    @MethodSource("transferAmounts")
    @DisplayName("Авторизованный пользователь может переводить деньги на счёт другого пользователя")
    public void authUserCanTransferMoneyToAnotherUserAccount(BigDecimal transferAmount) {

        CreateUserRequest createdUser1 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser1);

        CreateUserRequest createdUser2 = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser2);

        ValidatableResponse createAccountResponseOne = UserSteps.createAccount(createdUser1);
        long accountIdOne = UserSteps.getAccountID(createAccountResponseOne);

        ValidatableResponse createAccountResponseTwo = UserSteps.createAccount(createdUser2);
        long accountIdTwo = UserSteps.getAccountID(createAccountResponseTwo);

        // кладём денег чуть больше, чем собираемся переводить
        BigDecimal depositAmount = transferAmount.multiply(new BigDecimal("2"));
        AccountBalanceUtils.depositEnough(createdUser1, accountIdOne, depositAmount);

        BigDecimal senderAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser1.getUsername(), createdUser1.getPassword(), accountIdOne);
        BigDecimal receiverAccountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser2.getUsername(), createdUser2.getPassword(), accountIdTwo);

        TransferRequest transferRequestModel =
                TestDataFactory.createTransferModel(accountIdOne, accountIdTwo, transferAmount);
        UserSteps.makeTransfer(createdUser1, transferRequestModel);

        BigDecimal senderAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser1.getUsername(), createdUser1.getPassword(), accountIdOne);
        BigDecimal receiverAccountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser2.getUsername(), createdUser2.getPassword(), accountIdTwo);

        BigDecimal expectedSenderBalance = senderAccountBalanceBefore.subtract(transferAmount);
        BigDecimal expectedReceiverBalance = receiverAccountBalanceBefore.add(transferAmount);

        softly.assertThat(senderAccountBalanceAfter)
                .isEqualByComparingTo(expectedSenderBalance);
        softly.assertThat(receiverAccountBalanceAfter)
                .isEqualByComparingTo(expectedReceiverBalance);
    }
}