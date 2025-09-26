package tests;

import api.models.CreateUserRequest;
import api.models.CreateAccountResponse;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final List<CreateAccountResponse> accounts = new ArrayList<>();

    private SessionStorage() {}

    // Методы для работы с пользователями
    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }
    /**
     * Возвращем объект CreateUserRequest по его порядковому номеру, в списке созданноых пользователей.
     * @param namber Порядковый номер, начинаю с одного, а не с нуля.
     * @return Объект CreateUserRequest соответсвующий указанному порядковому номеру
     */
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet()).get(number - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static List<CreateUserRequest> getUsers() {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet());
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.values()).get(number - 1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    // Новые методы для работы с аккаунтами
    public static void addAccounts(List<CreateAccountResponse> accountsList) {
        INSTANCE.accounts.addAll(accountsList);
    }

    public static List<CreateAccountResponse> getAccounts() {
        return new ArrayList<>(INSTANCE.accounts);
    }

    public static CreateAccountResponse getAccount(int index) {
        return INSTANCE.accounts.get(index - 1);
    }

    public static CreateAccountResponse getFirstAccount() {
        return INSTANCE.accounts.isEmpty() ? null : INSTANCE.accounts.get(0);
    }

    // Универсальный метод для получения UserSteps (аналог из второго варианта)
    public static UserSteps getUserSteps(int userIndex) {
        return getSteps(userIndex);
    }

    public static void clear() {
        INSTANCE.userStepsMap.clear();
        INSTANCE.accounts.clear();
    }
}