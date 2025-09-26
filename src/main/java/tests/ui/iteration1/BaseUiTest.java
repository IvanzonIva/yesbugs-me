package tests.ui.iteration1;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.extensions.AccountSessionExtension;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
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

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true, "enableVideo", false));

        Configuration.browserCapabilities = capabilities;
    }

}
