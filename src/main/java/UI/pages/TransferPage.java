package UI.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import api.utils.RetryUtils;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TransferPage extends UserDashbord{

    private final SelenideElement transferAgainButton = $(Selectors.byXpath("//button[contains(text(), 'Transfer Again')]"));

    private final ElementsCollection senderAccountDropDown = $$(Selectors.byXpath("//select[contains(@class, 'account-selector')]/option"));
    private final SelenideElement recipientName = $(Selectors.byAttribute("placeholder",
            "Enter recipient name"));
    private final SelenideElement recipientAccountNumber = $(Selectors.byAttribute("placeholder",
            "Enter recipient account number"));
    private final SelenideElement amountField = $(Selectors.byAttribute("placeholder",
            "Enter amount"));
    private final SelenideElement confirmCheck = $(Selectors.byId("confirmCheck"));

    private final SelenideElement sendTransferButton = $(Selectors.byXpath("//button[contains(text(), 'Send Transfer')]"));

    public String url(){
        return "/transfer";
    }

    public TransferPage selectSenderAccount(String accountName){
        return RetryUtils.retry(
                () -> {
                    try {
                        System.out.println("🔍 Searching for account: " + accountName);

                        // Убеждаемся, что элементы загружены
                        if (senderAccountDropDown.isEmpty()) {
                            System.out.println("❌ No accounts found in dropdown");
                            return null;
                        }

                        // Кликаем на первый элемент чтобы открыть dropdown
                        senderAccountDropDown.get(0).click();

                        // Ищем нужный аккаунт
                        var accountOption = senderAccountDropDown.stream()
                                .filter(option -> {
                                    String optionText = option.getText();
                                    boolean matches = optionText.contains(accountName);
                                    System.out.println("   Comparing: '" + optionText + "' with '" + accountName + "' -> " + matches);
                                    return matches;
                                })
                                .findFirst();

                        if (accountOption.isPresent()) {
                            accountOption.get().click();
                            System.out.println("✅ Successfully selected account: " + accountName);
                            return this;
                        } else {
                            System.out.println("❌ Account not found: " + accountName);
                            return null;
                        }

                    } catch (Exception e) {
                        System.out.println("💥 Exception while selecting account: " + e.getMessage());
                        return null;
                    }
                },
                result -> result != null,
                3,
                2000
        );
    }

    public TransferPage enterRecipientName(String name){
        recipientName.sendKeys(name);
        return this;
    }

    public TransferPage enterRecipientAccount(String accountName){
        recipientAccountNumber.sendKeys(accountName);
        return this;
    }

    public TransferPage enterAmount(double amount){
        amountField.sendKeys(String.valueOf(amount));
        return this;
    }

    public TransferPage clickConfirm(){
        confirmCheck.click();
        return this;
    }

    public TransferPage cickSendTransfer(){
        sendTransferButton.click();
        return this;
    }

    public String getBankAccountText(String accountNumber){
        return senderAccountDropDown
                .stream().filter(option -> option.getText().contains(accountNumber))
                .findFirst().get().getText();
    }

    public TransactionsPage clickTransferAgain(){
        transferAgainButton.click();
        return new TransactionsPage();
    }
}