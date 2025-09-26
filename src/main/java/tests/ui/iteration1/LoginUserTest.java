package tests.ui.iteration1;

import UI.pages.AdminPanel;
import UI.pages.LoginPage;
import UI.pages.UserDashbord;
import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotation.Browsers;
import org.junit.jupiter.api.Test;

public class LoginUserTest extends BaseUiTest {

    @Test
    @Browsers({"chrome"})
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class).getAdminPanelText().shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = AdminSteps.createUser();

        new LoginPage().open().login(user.getUsername(), user.getPassword())
                .getPage(UserDashbord.class).getWelcomeText()
            .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}
