package tests.ui.iteration2.positive;

import UI.pages.UserDashbord;
import api.models.CreateAccountResponse;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.utils.TestDataFactory;
import common.annotation.AccountSession;
import common.annotation.UserSession;
import org.junit.jupiter.api.Test;
import tests.SessionStorage;
import tests.ui.iteration1.BaseUiTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferUiTest extends BaseUiTest {

    @Test
    @UserSession
    @AccountSession(value = 2) // Создаем 2 аккаунта
    public void transferValidAmount() {
        // Получаем созданные аккаунты
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);

        // Получаем пользователя для API операций
        var user = SessionStorage.getUser();

        // Пополняем аккаунт отправителя через API
        BigDecimal initialDeposit = TestDataFactory.getRandomDepositAmount();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit.doubleValue()
        );

        DepositResponse depositResponse = api.requests.steps.UserSteps.Deposit(user, depositRequest);

        // Получаем обновленные балансы через API
        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();
        CreateAccountResponse updatedReceiverAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = updatedReceiverAccount.getBalance();

        // Генерируем сумму для перевода (меньше или равная балансу отправителя)
        BigDecimal transferAmount = TestDataFactory.getRandomAmount(1.0, initialSenderBalance);

        // Test steps via UI
        new UserDashbord()
                .open()
                .makeATransfer() // Переходим на страницу переводов
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        // Проверка через API, что балансы изменились корректно
        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();

        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        double expectedSenderBalance = initialSenderBalance - transferAmount.doubleValue();
        double expectedReceiverBalance = initialReceiverBalance + transferAmount.doubleValue();

        assertThat(finalSenderAccount.getBalance())
                .as("Баланс отправителя должен уменьшиться на сумму перевода")
                .isEqualTo(expectedSenderBalance);

        assertThat(finalReceiverAccount.getBalance())
                .as("Баланс получателя должен увеличиться на сумму перевода")
                .isEqualTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferMaxAmount() {
        // Перевод максимально возможной суммы
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);

        var user = SessionStorage.getUser();

        // Пополняем аккаунт отправителя
        BigDecimal initialDeposit = new BigDecimal("1000.00");
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit.doubleValue()
        );
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        // Получаем обновленные балансы
        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = receiverAccount.getBalance();

        // Переводим всю сумму
        BigDecimal transferAmount = BigDecimal.valueOf(initialSenderBalance);

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        // Проверка через API
        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();

        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        double expectedSenderBalance = 0.0; // Весь баланс переведен
        double expectedReceiverBalance = initialReceiverBalance + transferAmount.doubleValue();

        assertThat(finalSenderAccount.getBalance())
                .as("Баланс отправителя должен быть равен 0 после перевода всей суммы")
                .isEqualTo(expectedSenderBalance);

        assertThat(finalReceiverAccount.getBalance())
                .as("Баланс получателя должен увеличиться на сумму перевода")
                .isEqualTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferWithRecipientName() {
        // Перевод с указанием имени получателя
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);

        var user = SessionStorage.getUser();

        // Пополняем аккаунт отправителя
        BigDecimal initialDeposit = TestDataFactory.getRandomDepositAmount();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit.doubleValue()
        );
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        // Получаем обновленные балансы
        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = receiverAccount.getBalance();

        // Генерируем сумму для перевода
        BigDecimal transferAmount = TestDataFactory.getRandomAmount(1.0, initialSenderBalance);

        // Используем имя пользователя из SessionStorage
        String recipientName = SessionStorage.getUser().getUsername();

        // Test steps via UI с указанием имени получателя
        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientName(recipientName)
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        // Проверка через API
        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();

        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        double expectedSenderBalance = initialSenderBalance - transferAmount.doubleValue();
        double expectedReceiverBalance = initialReceiverBalance + transferAmount.doubleValue();

        assertThat(finalSenderAccount.getBalance()).isEqualTo(expectedSenderBalance);
        assertThat(finalReceiverAccount.getBalance()).isEqualTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 3) // Создаем 3 аккаунта
    public void transferBetweenDifferentAccounts() {
        // Перевод между разными аккаунтами (не первым и вторым)
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(1); // Второй аккаунт
        CreateAccountResponse receiverAccount = accounts.get(2); // Третий аккаунт

        var user = SessionStorage.getUser();

        // Пополняем аккаунт отправителя
        BigDecimal initialDeposit = TestDataFactory.getRandomDepositAmount();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit.doubleValue()
        );
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        // Получаем обновленные балансы
        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        CreateAccountResponse updatedReceiverAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = updatedReceiverAccount.getBalance();

        // Генерируем сумму для перевода
        BigDecimal transferAmount = TestDataFactory.getRandomAmount(1.0, initialSenderBalance);

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        // Проверка через API
        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();

        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();

        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        double expectedSenderBalance = initialSenderBalance - transferAmount.doubleValue();
        double expectedReceiverBalance = initialReceiverBalance + transferAmount.doubleValue();

        assertThat(finalSenderAccount.getBalance()).isEqualTo(expectedSenderBalance);
        assertThat(finalReceiverAccount.getBalance()).isEqualTo(expectedReceiverBalance);
    }
}