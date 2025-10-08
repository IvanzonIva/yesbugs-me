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

public class DepositTest extends BaseTest {

    @Test
    public void authUserCanDepositMoneyWithValidAmount() {
        // Создаем пользователя и аккаунт
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        ValidatableResponse createAccountResponse = UserSteps.createAccount(createdUser);
        long accountId = UserSteps.getAccountID(createAccountResponse);

        // Получаем баланс до депозита
        double accountBalanceBefore = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(),
                createdUser.getPassword(),
                accountId);

        // Генерируем случайную валидную сумму
        BigDecimal depositAmount = TestDataFactory.getRandomDepositAmount();

        // Создаем запрос на депозит
        DepositRequest makeDeposit = TestDataFactory.createDepositModel(
                accountId,
                depositAmount.doubleValue());

        // Выполняем депозит
        DepositResponse responseModel = UserSteps.Deposit(createdUser, makeDeposit);

        // Получаем баланс после депозита
        double accountBalanceAfter = AccountBalanceUtils.getBalanceForAccount(
                createdUser.getUsername(),
                createdUser.getPassword(),
                accountId);

        // Сравнение балансов
        double expectedBalance = accountBalanceBefore + depositAmount.doubleValue();
        softly.assertThat(accountBalanceAfter)
                .as("Баланс аккаунта должен увеличиться на сумму депозита")
                .isEqualTo(expectedBalance);
    }
}