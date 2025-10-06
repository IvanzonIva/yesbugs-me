package tests.ui.iteration1;

import UI.pages.BankAlert;
import UI.pages.UserDashbord;
import api.models.CreateAccountResponse;
import common.annotation.UserSession;
import org.junit.jupiter.api.Test;
import tests.SessionStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountUiTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        new UserDashbord().open().createNewAccount();

        List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps().getAllAccounts();

        assertThat(createdAccounts).hasSize(1);

        new UserDashbord().checkAlertMessageAndAccept
                (BankAlert.NEW_ACCOUNT_CREATES.getMessage() + createdAccounts.getFirst().getAccountNumber());

        assertThat(createdAccounts.getFirst().getBalance()).isZero();

    }
}
