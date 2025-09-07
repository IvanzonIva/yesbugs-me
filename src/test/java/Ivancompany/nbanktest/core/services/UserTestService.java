package Ivancompany.nbanktest.core.services;

import Ivancompany.nbanktest.api.clients.UserAdminClient;

import java.util.List;

public class UserTestService {

    private final UserAdminClient userAdminClient;

    public UserTestService(UserAdminClient userAdminClient) {
        this.userAdminClient = userAdminClient;
    }

    public void safelyDeleteUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            safelyDeleteUser(userId);
        }
    }

    public void safelyDeleteUser(Long userId) {
        try {
            userAdminClient.deleteUser(userId);
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем выполнение
            System.err.println("Failed to delete user " + userId + ": " + e.getMessage());
        }
    }
}
