package UI.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class UserDashbord extends BasePage<UserDashbord> {
        private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
        private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
        private SelenideElement depositMoney = $(Selectors.byXpath("//*[contains(text(), 'Deposit Money')]"));
        private SelenideElement makeATransfer = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
        private SelenideElement profileName = $(Selectors.byClassName("user-name"));
        private SelenideElement homeButton = $(Selectors.byXpath("//button[contains(text(), 'Home')]"));

        @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashbord createNewAccount() {
       createNewAccount.click();
       return this;
    }

    public DepositPage depositMoney() {
        depositMoney.click();
        return new DepositPage();
    }

    public TransferPage makeATransfer() {
        makeATransfer.click();
        return new TransferPage();
    }

    public String getName() {
            return profileName.getText();
    }

    public UserDashbord goHome() {
            homeButton.click();
            return this;
    }

    public UserDashbord checkAlertAndAccept(String bankAlert) {
        Alert alert = switchTo().alert();
        String actualText = alert.getText();

        // 🔍 Выведем в лог для отладки
        System.out.println("Actual alert:   " + actualText);
        System.out.println("Expected alert: " + bankAlert);

        // 🔧 Приводим оба текста к единому формату с точкой
        String normalizedActual = actualText.replace(",", ".");
        String normalizedExpected = bankAlert.replace(",", ".");

        // 🔒 Сравниваем строго, как ты хотел
        assertThat(normalizedActual)
                .as("Alert text mismatch")
                .isEqualTo(normalizedExpected);

        alert.accept();
        return this;
    }


}
