package UI.pages;

import UI.elements.BaseElement;
import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import api.utils.RetryUtils;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePage <T extends BasePage>{
    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));


    public abstract String url();

    public T open(){
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {return  Selenide.page(pageClass);}

    public T checkAlertMessageAndAccept(String bankAlert) {
        // Используем перегрузку для Runnable (без возвращаемого значения)
        RetryUtils.retry(
                () -> {
                    Alert alert = Selenide.switchTo().alert();
                    assertThat(alert.getText()).contains(bankAlert);
                    alert.accept();
                },
                5, // 5 попыток
                1000 // 1 секунда между попытками
        );

        return (T) this;
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

    }

    public static void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(),createUserRequest.getPassword());
    }

    // ElementCollection -> List<BaseElement>

    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
        return elementsCollection.stream().map(constructor).toList();
    }

}
