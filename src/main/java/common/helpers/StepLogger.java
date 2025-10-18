package common.helpers;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;

/**
 * StepLogger — обёртка для шагов Allure с автоматическим логированием и скриншотами (только для UI-тестов).
 *
 * ✅ Создаёт шаги в Allure.
 * ✅ Делает скриншоты после каждого успешного шага.
 * ✅ Делает скриншоты при ошибке.
 * ✅ Не делает скриншоты, если WebDriver не запущен (например, в API-тестах).
 *
 * Пример использования:
 * StepLogger.log("Ввести логин", () -> $("#username").setValue("admin"));
 */
public class StepLogger {

    @FunctionalInterface
    public interface ThrowableRunnable<T> {
        T run() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowableVoidRunnable {
        void run() throws Throwable;
    }

    public static <T> T log(String title, ThrowableRunnable<T> runnable) {
        return Allure.step(title, () -> {
            try {
                T result = runnable.run();
                attachIfUi("📸 После шага: " + title);
                return result;
            } catch (Throwable t) {
                attachIfUi("❌ Ошибка на шаге: " + title);
                throw t;
            }
        });
    }

    public static void log(String title, ThrowableVoidRunnable runnable) {
        Allure.step(title, () -> {
            try {
                runnable.run();
                attachIfUi("📸 После шага: " + title);
            } catch (Throwable t) {
                attachIfUi("❌ Ошибка на шаге: " + title);
                throw t;
            }
            return null;
        });
    }

    private static void attachIfUi(String name) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Attachments.attachScreenshot(name);
        }
    }
}
