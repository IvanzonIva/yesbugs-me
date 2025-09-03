package Ivancompany.nbanktest.tests.functional.base;

import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.utils.AuthHelper;
import Ivancompany.nbanktest.core.utils.DataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
// Добавьте эти импорты
import java.util.ArrayList;
import java.util.List;

public class SingleUserTestBase extends ApiTestBase {
    protected String username;
    protected String password;
    protected String userAuthHeader;
    protected Long accountId;
    protected Long userId;

    // Сохраняем возможность управления несколькими пользователями
    protected final List<Long> createdUserIds = new ArrayList<>();

    @BeforeEach
    void createTestUser() {
        username = DataGenerator.generateValidUsername();
        password = DataGenerator.generateValidPassword();

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role("USER")
                .build();

        UserResponse userResponse = userAdminClient.createUser(userRequest);
        userId = userResponse.getId();
        createdUserIds.add(userId); // сохраняем для удаления

        userAuthHeader = AuthHelper.generateBasicAuthHeader(username, password);

        var accountResponse = accountClient.createAccount(userAuthHeader);
        accountId = accountResponse.getId();
    }

    @AfterEach
    void deleteTestUsers() {
        List<Long> failedDeletions = new ArrayList<>();

        for (Long id : createdUserIds) {
            try {
                userAdminClient.deleteUser(id);
            } catch (Exception e) {
                failedDeletions.add(id);
                // Логируем ошибку, но не прерываем выполнение
                System.err.println("Failed to delete user " + id + ": " + e.getMessage());
            }
        }

        createdUserIds.clear();
        createdUserIds.addAll(failedDeletions); // сохраняем неудаленные ID для возможной повторной попытки
    }

    // Метод для создания дополнительных пользователей в тестах-наследниках
    protected UserTestData createAdditionalUser(String role) {
        String newUsername = DataGenerator.generateValidUsername();
        String newPassword = DataGenerator.generateValidPassword();

        UserResponse userResponse = userAdminClient.createUser(CreateUserRequest.builder()
                .username(newUsername)
                .password(newPassword)
                .role(role)
                .build());

        Long newUserId = userResponse.getId();
        createdUserIds.add(newUserId);

        String newAuthHeader = AuthHelper.generateBasicAuthHeader(newUsername, newPassword);
        var accountResponse = accountClient.createAccount(newAuthHeader);

        return new UserTestData(newUserId, newUsername, newPassword, newAuthHeader, accountResponse.getId());
    }

    protected record UserTestData(Long userId, String username, String password, String authHeader, Long accountId) {}
}