package tests.ui.iteration2.negative;

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
public class DepositNegativeUiTest extends BaseUiTest {

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenDepositInvalidAmount() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        double initialBalance = account.getBalance();

        // Генерируем недопустимую сумму (больше 5000)
        BigDecimal invalidAmount = TestDataFactory.getRandomAmount(5000.01, 10000.0);

        // Test steps via UI
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                .enterAmount(invalidAmount.doubleValue())
                .clickDeposit()
                .checkAlertAndAccept("❌ Please deposit less or equal to 5000$.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при недопустимой сумме депозита")
                .isEqualTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenAccountNotSelected() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        double initialBalance = account.getBalance();

        // Генерируем валидную сумму
        BigDecimal validAmount = TestDataFactory.getRandomDepositAmount();

        // Test steps via UI - НЕ выбираем аккаунт
        new UserDashbord()
                .open()
                .depositMoney()
                // Пропускаем selectAccount - аккаунт не выбран
                .enterAmount(validAmount.doubleValue())
                .clickDeposit()
                .checkAlertAndAccept("❌ Please select an account.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при невыбранном аккаунте")
                .isEqualTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenAmountFieldIsEmpty() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        double initialBalance = account.getBalance();

        // Test steps via UI - НЕ вводим сумму
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                // Пропускаем enterAmount - поле суммы пустое
                .clickDeposit()
                .checkAlertAndAccept("❌ Please enter a valid amount.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при пустом поле суммы")
                .isEqualTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenDepositZeroAmount() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        double initialBalance = account.getBalance();

        // Test steps via UI - вводим 0
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                .enterAmount(0.0)
                .clickDeposit()
                .checkAlertAndAccept("❌ Please enter a valid amount.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при нулевой сумме депозита")
                .isEqualTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenDepositNegativeAmount() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        double initialBalance = account.getBalance();

        // Генерируем отрицательную сумму
        BigDecimal negativeAmount = TestDataFactory.getRandomAmount(-1000.0, -0.01);

        // Test steps via UI
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                .enterAmount(negativeAmount.doubleValue())
                .clickDeposit()
                .checkAlertAndAccept("❌ Please enter a valid amount.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при отрицательной сумме депозита")
                .isEqualTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2) // Создаем 2 аккаунта
    public void depositToSpecificAccountOtherAccountUnaffected() {
        // Получаем созданные аккаунты
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse firstAccount = accounts.get(0);
        CreateAccountResponse secondAccount = accounts.get(1);

        double initialBalanceFirst = firstAccount.getBalance();
        double initialBalanceSecond = secondAccount.getBalance();

        // Генерируем валидную сумму
        BigDecimal depositAmount = TestDataFactory.getRandomDepositAmount();

        // Вносим депозит на второй аккаунт
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(secondAccount.getAccountNumber())
                .enterAmount(depositAmount.doubleValue())
                .clickDeposit()
                .checkAlertAndAccept(String.format("✅ Successfully deposited $%s to account %s!",
                        depositAmount, secondAccount.getAccountNumber()));

        // Проверяем через API, что баланс второго аккаунта увеличился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();

        CreateAccountResponse updatedSecondAccount = accountsAfterDeposit.stream()
                .filter(acc -> acc.getId() == secondAccount.getId())
                .findFirst()
                .orElseThrow();

        double expectedBalanceSecond = initialBalanceSecond + depositAmount.doubleValue();
        assertThat(updatedSecondAccount.getBalance())
                .as("Баланс второго аккаунта должен увеличиться")
                .isEqualTo(expectedBalanceSecond);

        // Проверяем, что баланс первого аккаунта не изменился
        CreateAccountResponse updatedFirstAccount = accountsAfterDeposit.stream()
                .filter(acc -> acc.getId() == firstAccount.getId())
                .findFirst()
                .orElseThrow();

        assertThat(updatedFirstAccount.getBalance())
                .as("Баланс первого аккаунта не должен измениться")
                .isEqualTo(initialBalanceFirst);
    }
}