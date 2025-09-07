package Ivancompany.nbanktest.tests.functional.base;

import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.models.Role;
import Ivancompany.nbanktest.core.utils.AuthHelper;
import Ivancompany.nbanktest.core.utils.DataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
                .role(Role.USER)
                .build();

        UserResponse userResponse = userAdminClient.createUser(userRequest);
        userId = userResponse.getId();
        createdUserIds.add(userId);

        userAuthHeader = AuthHelper.generateBasicAuthHeader(username, password);

        var accountResponse = accountClient.createAccount(userAuthHeader);
        accountId = accountResponse.getId();
    }

    @AfterEach
    void deleteTestUsers() {
        // Используем сервисный класс для удаления пользователей
        userTestService.safelyDeleteUsers(createdUserIds);
        createdUserIds.clear();
    }

    // Метод для создания дополнительных пользователей в тестах-наследниках
    protected UserTestData createAdditionalUser(Role role) {
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