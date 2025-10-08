package common.extensions;

import UI.pages.BasePage;
import api.models.CreateUserRequest;
import common.annotation.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AdminSessionExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        // Шаг 1: Проверка, есть ли у теста аннотация AdminSession
        AdminSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if (annotation != null) { // Шаг 2: если есть, добавляем в local storage токен админа
            BasePage.authAsUser(CreateUserRequest.getAdmin());
        }
    }
}
