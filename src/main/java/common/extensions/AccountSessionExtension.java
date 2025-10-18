package common.extensions;

import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.requests.steps.UserSteps;
import common.annotation.AccountSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import common.storage.SessionStorage;

import java.util.LinkedList;
import java.util.List;

public class AccountSessionExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        AccountSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(AccountSession.class);
        if (annotation != null) {
            int accountCount = annotation.value();
            int forUserIndex = annotation.forUser();

            // Проверяем, что пользователь уже создан
            if (SessionStorage.getUsers().size() < forUserIndex) {
                throw new IllegalStateException("Пользователь с индексом " + forUserIndex + " не найден в SessionStorage");
            }

            // Получаем пользователя, для которого создаем аккаунты
            CreateUserRequest user = SessionStorage.getUser(forUserIndex);

            List<CreateAccountResponse> accounts = new LinkedList<>();

            // Создаем указанное количество аккаунтов через UserSteps
            for (int i = 0; i < accountCount; i++) {
                // Используем существующий метод createAccount из UserSteps
                CreateAccountResponse account = createAccountViaUserSteps(user);
                accounts.add(account);
            }

            // Сохраняем аккаунты в SessionStorage
            SessionStorage.addAccounts(accounts);
        }
    }

    private CreateAccountResponse createAccountViaUserSteps(CreateUserRequest user) {
        // Вызываем метод createAccount из UserSteps и преобразуем ответ
        var response = UserSteps.createAccount(user);

        return response.extract().as(CreateAccountResponse.class);
    }
}