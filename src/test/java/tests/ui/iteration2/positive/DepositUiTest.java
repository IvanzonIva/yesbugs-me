package tests.ui.iteration2.positive;

import UI.pages.UserDashbord;
import api.models.CreateAccountResponse;
import api.utils.TestDataFactory;
import common.annotation.AccountSession;
import common.annotation.UserSession;
import org.junit.jupiter.api.Test;
import common.storage.SessionStorage;
import tests.ui.iteration1.BaseUiTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositUiTest extends BaseUiTest {
//Баг, при пополнении на сумму 3175.03 получил баланс 3175.0
    @Test
    @UserSession // Создает пользователя
    @AccountSession(value = 1) // Создает 1 аккаунт через API
    public void depositValidAmount() {
        // Получаем созданный аккаунт через SessionStorage
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        double initialBalance = account.getBalance();

        // Генерируем случайную сумму для депозита
        BigDecimal depositAmount = TestDataFactory.getRandomDepositAmount();

        // Test steps via UI
        new UserDashbord()
                .open()
                .depositMoney() // Переходим на страницу депозита
                .selectAccount(account.getAccountNumber()) // Выбираем аккаунт по номеру
                .enterAmount(depositAmount.doubleValue()) // Вводим случайную сумму
                .clickDeposit() // Нажимаем кнопку Deposit
                .checkAlertAndAccept(String.format("✅ Successfully deposited $%s to account %s!",
                        depositAmount, account.getAccountNumber())); // Проверяем алерт

        // Проверка, что балан
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        double expectedBalance = initialBalance + depositAmount.doubleValue();
        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта должен увеличиться на сумму депозита")
                .isEqualTo(expectedBalance);
    }
}