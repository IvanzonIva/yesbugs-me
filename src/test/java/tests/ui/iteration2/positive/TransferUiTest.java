package tests.ui.iteration2.positive;

import UI.pages.UserDashbord;
import api.models.CreateAccountResponse;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.utils.TestDataFactory;
import common.annotation.AccountSession;
import common.annotation.UserSession;
import org.junit.jupiter.api.Test;
import common.storage.SessionStorage;
import tests.ui.iteration1.BaseUiTest;

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

        double initialDeposit = TestDataFactory.getRandomDepositAmount().doubleValue();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit
        );
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

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = updatedReceiverAccount.getBalance();

        double transferAmount = TestDataFactory.getRandomAmount(1.0, initialSenderBalance).doubleValue();

        new UserDashbord()
                .open()
                .makeATransfer()
                .selectSenderAccount(senderAccount.getAccountNumber())
                .enterRecipientAccount(receiverAccount.getAccountNumber())
                .enterAmount(transferAmount)
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("✅ Successfully transferred $%s to account %s!",
                        transferAmount, receiverAccount.getAccountNumber()
                ));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        double expectedSenderBalance = initialSenderBalance - transferAmount;
        double expectedReceiverBalance = initialReceiverBalance + transferAmount;

        assertThat(finalSenderAccount.getBalance()).isEqualTo(expectedSenderBalance);
        assertThat(finalReceiverAccount.getBalance()).isEqualTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferMaxAmount() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);
        var user = SessionStorage.getUser();

        double initialDeposit = 1000.00;
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit
        );
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = receiverAccount.getBalance();

        double transferAmount = initialSenderBalance;

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
                        transferAmount, receiverAccount.getAccountNumber()
                ));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        double expectedSenderBalance = 0.0;
        double expectedReceiverBalance = initialReceiverBalance + transferAmount;

        assertThat(finalSenderAccount.getBalance()).isEqualTo(expectedSenderBalance);
        assertThat(finalReceiverAccount.getBalance()).isEqualTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 2)
    public void transferWithRecipientName() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(0);
        CreateAccountResponse receiverAccount = accounts.get(1);
        var user = SessionStorage.getUser();

        double initialDeposit = TestDataFactory.getRandomDepositAmount().doubleValue();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit
        );
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = receiverAccount.getBalance();

        double transferAmount = TestDataFactory.getRandomAmount(1.0, initialSenderBalance).doubleValue();
        String recipientName = SessionStorage.getUser().getUsername();

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
                        transferAmount, receiverAccount.getAccountNumber()
                ));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        double expectedSenderBalance = initialSenderBalance - transferAmount;
        double expectedReceiverBalance = initialReceiverBalance + transferAmount;

        assertThat(finalSenderAccount.getBalance()).isEqualTo(expectedSenderBalance);
        assertThat(finalReceiverAccount.getBalance()).isEqualTo(expectedReceiverBalance);
    }

    @Test
    @UserSession
    @AccountSession(value = 3)
    public void transferBetweenDifferentAccounts() {
        List<CreateAccountResponse> accounts = SessionStorage.getAccounts();
        CreateAccountResponse senderAccount = accounts.get(1);
        CreateAccountResponse receiverAccount = accounts.get(2);
        var user = SessionStorage.getUser();

        double initialDeposit = TestDataFactory.getRandomDepositAmount().doubleValue();
        DepositRequest depositRequest = TestDataFactory.createDepositModel(
                senderAccount.getId(),
                initialDeposit
        );
        api.requests.steps.UserSteps.Deposit(user, depositRequest);

        List<CreateAccountResponse> updatedAccounts = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse updatedSenderAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse updatedReceiverAccount = updatedAccounts.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        double initialSenderBalance = updatedSenderAccount.getBalance();
        double initialReceiverBalance = updatedReceiverAccount.getBalance();

        double transferAmount = TestDataFactory.getRandomAmount(1.0, initialSenderBalance).doubleValue();

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
                        transferAmount, receiverAccount.getAccountNumber()
                ));

        List<CreateAccountResponse> accountsAfterTransfer = SessionStorage.getSteps().getAllAccounts();
        CreateAccountResponse finalSenderAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == senderAccount.getId())
                .findFirst().orElseThrow();
        CreateAccountResponse finalReceiverAccount = accountsAfterTransfer.stream()
                .filter(acc -> acc.getId() == receiverAccount.getId())
                .findFirst().orElseThrow();

        double expectedSenderBalance = initialSenderBalance - transferAmount;
        double expectedReceiverBalance = initialReceiverBalance + transferAmount;

        assertThat(finalSenderAccount.getBalance()).isEqualTo(expectedSenderBalance);
        assertThat(finalReceiverAccount.getBalance()).isEqualTo(expectedReceiverBalance);
    }
}
