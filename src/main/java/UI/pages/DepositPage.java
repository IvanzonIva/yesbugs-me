package UI.pages;

import com.codeborne.selenide.*;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class DepositPage extends UserDashbord {

    private ElementsCollection accountsOptions = $$(Selectors.byXpath("//select[contains(@class, 'account-selector')]/option"));
    private SelenideElement amountField = $(Selectors.byClassName("deposit-input"));
    private SelenideElement depositButton = $(Selectors.byXpath("//button[contains(text(), 'Deposit')]"));

    @Override
    public String url() {
        return "/deposit";
    }

    // Добавляем метод ожидания загрузки страницы
    public DepositPage waitForPageToLoad() {
        amountField.shouldBe(visible, Duration.ofSeconds(10));
        depositButton.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    public DepositPage selectAccount(String accountName) {
        // Ждем появления выпадающего списка
        $(Selectors.byXpath("//select[contains(@class, 'account-selector')]"))
                .shouldBe(visible, Duration.ofSeconds(15));

        // Ждем появления конкретной опции с нужным текстом
        SelenideElement targetOption = $(Selectors.byXpath(
                "//select[contains(@class, 'account-selector')]/option[contains(text(), '" + accountName + "')]"))
                .shouldBe(visible, Duration.ofSeconds(15));

        // Кликаем
        targetOption.shouldBe(interactable, Duration.ofSeconds(5)).click();
        return this;
    }

    public ElementsCollection getAccountOptions() {
        return accountsOptions;
    }

    public DepositPage enterAmount(double amount) {
        amountField.setValue(Double.toString(amount));
        return this;
    }

    public DepositPage clickDeposit() {
        depositButton.click();
        return this;
    }

}
