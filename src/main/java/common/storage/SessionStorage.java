package common.storage;

import api.models.CreateUserRequest;
import api.models.CreateAccountResponse;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
    /*Thread Local - способ сделать SessionStorage потокобезопасным
     Каждый поток обращаясь к INSTANCE.get() получают свою КОПИЮ
     Под копотом храниться Map<Thread, SessionStorage>

     Тест1 - Создал юзеров, положил в SessionStorage (СВОЯ КОПИЯ), работает с ними
     Тест2(паралельно) - Создал юзеров, положил в SessionStorage (СВОЯ КОПИЯ), работает с ним
     Тест3(паралельно) - Создал юзеров, положил в SessionStorage (СВОЯ КОПИЯ), работает с ним

     */
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
    /**
     * Возвращем объект CreateUserRequest по его порядковому номеру, в списке созданноых пользователей.
     * @param namber Порядковый номер, начинаю с одного, а не с нуля.
     * @return Объект CreateUserRequest соответсвующий указанному порядковому номеру
     */
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

    // Новые методы для работы с аккаунтами
    public static void addAccounts(List<CreateAccountResponse> accountsList) {
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

    // Универсальный метод для получения UserSteps (аналог из второго варианта)
    public static UserSteps getUserSteps(int userIndex) {
        return getSteps(userIndex);
    }

    public static void clear() {
        INSTANCE.get().accounts.clear();
    }
}