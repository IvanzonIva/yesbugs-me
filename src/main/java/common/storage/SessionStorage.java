package common.storage;

import api.models.CreateUserRequest;
import api.models.CreateAccountResponse;
import api.requests.steps.UserSteps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final List<CreateAccountResponse> accounts = new ArrayList<>();

    private SessionStorage() {}

    // Методы для работы с пользователями
    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.keySet()).get(number - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static List<CreateUserRequest> getUsers() {
        return new ArrayList<>(INSTANCE.get().userStepsMap.keySet());
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.values()).get(number - 1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    // Методы для работы с аккаунтами
    public static void addAccounts(List<CreateAccountResponse> accountsList) {
        // Приводим все балансы к BigDecimal с точностью 2 знака
        for (CreateAccountResponse account : accountsList) {
            if (account.getBalance() == null) {
                account.setBalance(BigDecimal.ZERO.setScale(2));
            } else {
                account.setBalance(account.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        INSTANCE.get().accounts.addAll(accountsList);
    }

    public static List<CreateAccountResponse> getAccounts() {
        return new ArrayList<>(INSTANCE.get().accounts);
    }

    public static CreateAccountResponse getAccount(int index) {
        return INSTANCE.get().accounts.get(index - 1);
    }

    public static CreateAccountResponse getFirstAccount() {
        return INSTANCE.get().accounts.isEmpty() ? null : INSTANCE.get().accounts.get(0);
    }

    // Универсальный метод для получения UserSteps
    public static UserSteps getUserSteps(int userIndex) {
        return getSteps(userIndex);
    }

    public static void clear() {
        INSTANCE.get().accounts.clear();
    }
}
