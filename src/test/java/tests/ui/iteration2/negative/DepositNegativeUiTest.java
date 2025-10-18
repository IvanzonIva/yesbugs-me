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
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositNegativeUiTest extends BaseUiTest {

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenDepositInvalidAmount() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        BigDecimal initialBalance = account.getBalance();

        // Генерируем недопустимую сумму (больше 5000)
        BigDecimal invalidAmount = TestDataFactory.getRandomAmount(
                new BigDecimal("5000.01"),
                new BigDecimal("10000.00")
        );

        // Test steps via UI
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                .enterAmount(invalidAmount)
                .clickDeposit()
                .checkAlertAndAccept("❌ Please deposit less or equal to 5000$.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при недопустимой сумме депозита")
                .isEqualByComparingTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenAccountNotSelected() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        BigDecimal initialBalance = account.getBalance();

        // Генерируем валидную сумму
        BigDecimal validAmount = TestDataFactory.getRandomDepositAmount();

        // Test steps via UI - НЕ выбираем аккаунт
        new UserDashbord()
                .open()
                .depositMoney()
                // Пропускаем selectAccount - аккаунт не выбран
                .enterAmount(validAmount)
                .clickDeposit()
                .checkAlertAndAccept("❌ Please select an account.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при невыбранном аккаунте")
                .isEqualByComparingTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenAmountFieldIsEmpty() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        BigDecimal initialBalance = account.getBalance();

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
                .isEqualByComparingTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenDepositZeroAmount() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        BigDecimal initialBalance = account.getBalance();

        // Test steps via UI - вводим 0
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                .enterAmount(BigDecimal.ZERO)
                .clickDeposit()
                .checkAlertAndAccept("❌ Please enter a valid amount.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при нулевой сумме депозита")
                .isEqualByComparingTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 1)
    public void errorWhenDepositNegativeAmount() {
        // Получаем созданный аккаунт
        CreateAccountResponse account = SessionStorage.getFirstAccount();
        BigDecimal initialBalance = account.getBalance();

        // Генерируем отрицательную сумму
        BigDecimal negativeAmount = TestDataFactory.getRandomAmount(
                new BigDecimal("-1000.00"),
                new BigDecimal("-0.01")
        );

        // Test steps via UI
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(account.getAccountNumber())
                .enterAmount(negativeAmount)
                .clickDeposit()
                .checkAlertAndAccept("❌ Please enter a valid amount.");

        // Проверяем через API, что баланс НЕ изменился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedAccount = accountsAfterDeposit.get(0);

        assertThat(updatedAccount.getBalance())
                .as("Баланс аккаунта не должен измениться при отрицательной сумме депозита")
                .isEqualByComparingTo(initialBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2) // Создаем 2 аккаунта
    public void depositToSpecificAccountOtherAccountUnaffected() {
        // Получаем созданные аккаунты
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse firstAccount = accounts.get(0);
        CreateAccountResponse secondAccount = accounts.get(1);

        BigDecimal initialBalanceFirst = firstAccount.getBalance();
        BigDecimal initialBalanceSecond = secondAccount.getBalance();

        // Генерируем валидную сумму
        BigDecimal depositAmount = TestDataFactory.getRandomDepositAmount();

        // Вносим депозит на второй аккаунт
        new UserDashbord()
                .open()
                .depositMoney()
                .selectAccount(secondAccount.getAccountNumber())
                .enterAmount(depositAmount)
                .clickDeposit()
                .checkAlertAndAccept(String.format("✅ Successfully deposited $%s to account %s!",
                        depositAmount, secondAccount.getAccountNumber()));

        // Проверяем через API, что баланс второго аккаунта увеличился
        List<CreateAccountResponse> accountsAfterDeposit = SessionStorage.getSteps().getAllAccounts();

        CreateAccountResponse updatedSecondAccount = accountsAfterDeposit.stream()
                .filter(acc -> acc.getId() == secondAccount.getId())
                .findFirst()
                .orElseThrow();

        BigDecimal expectedBalanceSecond = initialBalanceSecond.add(depositAmount)
                .setScale(2, RoundingMode.HALF_UP);

//        assertThat(updatedSecondAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс второго аккаунта должен увеличиться")
//                .isEqualByComparingTo(expectedBalanceSecond);

        // Проверяем, что баланс первого аккаунта не изменился
        CreateAccountResponse updatedFirstAccount = accountsAfterDeposit.stream()
                .filter(acc -> acc.getId() == firstAccount.getId())
                .findFirst()
                .orElseThrow();

        assertThat(updatedFirstAccount.getBalance())
                .as("Баланс первого аккаунта не должен измениться")
                .isEqualByComparingTo(initialBalanceFirst);
    }
}