package tests.ui.iteration1;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import common.extensions.AccountSessionExtension;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.DesiredCapabilities;
import tests.api.iteration1.BaseTest;

import java.util.Map;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(AccountSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)
public class BaseUiTest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserVersion = Config.getProperty("browserVersion");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.headless = true;

        // Увеличиваем таймауты для стабильности
        Configuration.timeout = 2000;
        Configuration.pageLoadTimeout = 5000;

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.of(
                "enableVNC", true,
                "enableLog", true,
                "enableVideo", false
        ));

        Configuration.browserCapabilities = capabilities;
    }

    @AfterEach
    public void tearDown() {
        // Очищаем браузер после каждого теста
        Selenide.closeWebDriver();

        // Очищаем сессионные данные
        clearSessionData();
    }

    private void clearSessionData() {
        // Очистка cookies и localStorage
        try {
            Selenide.clearBrowserCookies();
            Selenide.clearBrowserLocalStorage();
        } catch (Exception e) {
            // Игнорируем ошибки очистки, если браузер уже закрыт
        }
    }
}