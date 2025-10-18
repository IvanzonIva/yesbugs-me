package tests.ui.iteration2.positive;

import UI.pages.UserDashbord;
import api.models.CreateAccountResponse;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.TransferRequest;
import api.utils.TestDataFactory;
import common.annotation.AccountSession;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tests.ui.iteration1.BaseUiTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferUiTest extends BaseUiTest {

    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferValidAmount() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);
        var user = SessionStorage.getUser();

        BigDecimal initialDeposit = TestDataFactory.getRandomDepositAmount();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(senderAccount.getId(), initialDeposit);
        DepositResponse depositResponse = api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst()
                .orElseThrow();
        CreateAccountResponse updatedReceiverAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst()
                .orElseThrow();

        BigDecimal initialSenderBalance = updatedSenderAccount.getBalance();
        BigDecimal initialReceiverBalance = updatedReceiverAccount.getBalance();

        // Используем обновленный метод TestDataFactory для генерации случайной суммы
        BigDecimal transferAmount = TestDataFactory.getRandomAmount(BigDecimal.ONE, initialSenderBalance);
        TransferRequest transferRequest = TestDataFactory.createTransferModel(
                senderAccount.getId(),
                receiverAccount.getId(),
                transferAmount
        );

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount)
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("✅ Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal expectedSenderBalance = initialSenderBalance.subtract(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedReceiverBalance = initialReceiverBalance.add(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);

//        assertThat(finalSenderAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс отправителя после перевода")
//                .isEqualByComparingTo(expectedSenderBalance);
//        assertThat(finalReceiverAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс получателя после перевода")
//                .isEqualByComparingTo(expectedReceiverBalance);
    }

//    @Disabled("В карантине")
    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferMaxAmount() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);
        var user = SessionStorage.getUser();

        BigDecimal initialDeposit = new BigDecimal("1000.00");
        DepositRequest depositRequest = TestDataFactory.createDepositModel(senderAccount.getId(), initialDeposit);
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal initialSenderBalance = updatedSenderAccount.getBalance();
        BigDecimal initialReceiverBalance = receiverAccount.getBalance();

        BigDecimal transferAmount = initialSenderBalance;
        TransferRequest transferRequest = TestDataFactory.createTransferModel(
                senderAccount.getId(),
                receiverAccount.getId(),
                transferAmount
        );

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount)
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("✅ Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal expectedSenderBalance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedReceiverBalance = initialReceiverBalance.add(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);

//        assertThat(finalSenderAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс отправителя после максимального перевода")
//                .isEqualByComparingTo(expectedSenderBalance);
//        assertThat(finalReceiverAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс получателя после максимального перевода")
//                .isEqualByComparingTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferWithRecipientName() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);
        var user = SessionStorage.getUser();

        BigDecimal initialDeposit = TestDataFactory.getRandomDepositAmount();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(senderAccount.getId(), initialDeposit);
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal initialSenderBalance = updatedSenderAccount.getBalance();
        BigDecimal initialReceiverBalance = receiverAccount.getBalance();

        // Используем обновленный метод TestDataFactory
        BigDecimal transferAmount = TestDataFactory.getRandomAmount(BigDecimal.ONE, initialSenderBalance);
        String recipientName = SessionStorage.getUser().getUsername();
        TransferRequest transferRequest = TestDataFactory.createTransferModel(
                senderAccount.getId(),
                receiverAccount.getId(),
                transferAmount
        );

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientName(recipientName)
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount)
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("✅ Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal expectedSenderBalance = initialSenderBalance.subtract(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedReceiverBalance = initialReceiverBalance.add(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);

//        assertThat(finalSenderAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс отправителя после перевода с именем получателя")
//                .isEqualByComparingTo(expectedSenderBalance);
//        assertThat(finalReceiverAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс получателя после перевода с именем получателя")
//                .isEqualByComparingTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 3)
    public void transferBetweenDifferentAccounts() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(1);
        CreateAccountResponse receiverAccount = accounts.get(2);
        var user = SessionStorage.getUser();

        BigDecimal initialDeposit = TestDataFactory.getRandomDepositAmount();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(senderAccount.getId(), initialDeposit);
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse updatedReceiverAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal initialSenderBalance = updatedSenderAccount.getBalance();
        BigDecimal initialReceiverBalance = updatedReceiverAccount.getBalance();

        // Используем обновленный метод TestDataFactory
        BigDecimal transferAmount = TestDataFactory.getRandomAmount(BigDecimal.ONE, initialSenderBalance);
        TransferRequest transferRequest = TestDataFactory.createTransferModel(
                senderAccount.getId(),
                receiverAccount.getId(),
                transferAmount
        );

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount)
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format(
                        "✅ Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        BigDecimal expectedSenderBalance = initialSenderBalance.subtract(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedReceiverBalance = initialReceiverBalance.add(transferAmount)
                .setScale(2, RoundingMode.HALF_UP);

//        assertThat(finalSenderAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс отправителя после перевода между разными аккаунтами")
//                .isEqualByComparingTo(expectedSenderBalance);
//        assertThat(finalReceiverAccount.getBalance().setScale(2, RoundingMode.HALF_UP))
//                .as("Баланс получателя после перевода между разными аккаунтами")
//                .isEqualByComparingTo(expectedReceiverBalance);
    }
}