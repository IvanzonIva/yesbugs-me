package Ivancompany.nbanktest.core.utils;

import Ivancompany.nbanktest.api.clients.AccountClient;
import Ivancompany.nbanktest.api.clients.UserAdminClient;
import Ivancompany.nbanktest.api.dto.request.CreateUserRequest;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.models.Role;

public class UserTestHelper {
    private static final UserAdminClient userAdminClient = new UserAdminClient();
    private static final AccountClient accountClient = new AccountClient();

    public static UserTestData createUserWithAccount(Role role) {
        try {
            String username = DataGenerator.generateValidUsername();
            String password = DataGenerator.generateValidPassword();

            UserResponse userResponse = userAdminClient.createUser(CreateUserRequest.builder()
                    .username(username)
                    .password(password)
                    .role(role)
                    .build());

            String authHeader = AuthHelper.generateBasicAuthHeader(username, password);
            var accountResponse = accountClient.createAccount(authHeader);

            return new UserTestData(
                    userResponse.getId(),
                    username,
                    password,
                    authHeader,
                    accountResponse.getId()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user with account: " + e.getMessage(), e);
        }
    }

    public record UserTestData(Long userId, String username, String password, String authHeader, Long accountId) {}
}