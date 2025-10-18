package tests.api.iteration2.positive;

import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import tests.api.iteration1.BaseTest;
import api.utils.AccountBalanceUtils;
import api.utils.TestDataFactory;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DepositTest extends BaseTest {

    @Test
    public void authUserCanDepositMoneyWithValidAmount() {
        // Создаем пользователя и аккаунт
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        // Получаем баланс до депозита
        BigDecimal accountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(),
                createdUser.getPassword(),
                accountId);

        // Генерируем случайную валидную сумму
        BigDecimal depositAmount = TestDataFactory.getRandomDepositAmount();

        // Создаем запрос на депозит
        DepositRequest makeDeposit = TestDataFactory.createDepositModel(
                accountId,
                depositAmount);

        // Выполняем депозит
        DepositResponse responseModel = UserSteps.Deposit(createdUser, makeDeposit);

        // Получаем баланс после депозита
        BigDecimal accountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(),
                createdUser.getPassword(),
                accountId);

        // Сравнение балансов
        BigDecimal expectedBalance = accountBalanceBefore.add(depositAmount)
                .setScale(2, RoundingMode.HALF_UP);

        softly.assertThat(accountBalanceAfter.setScale(2, RoundingMode.HALF_UP))
                .as("Баланс аккаунта должен увеличиться на сумму депозита")
                .isEqualByComparingTo(expectedBalance);
    }
}