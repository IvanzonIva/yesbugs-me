package Ivancompany.nbanktest.core.utils;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class AuthHelper {

    private static final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();

    public static String generateBasicAuthHeader(String username, String password) {
        String key = username + ":" + password;
        return tokenCache.computeIfAbsent(key, k -> {
            String encoded = Base64.getEncoder().encodeToString(k.getBytes());
            return "Basic " + encoded;
        });
    }

    // Можно добавить метод для очистки кеша при необходимости
    // public static void clearCache() {
    //    tokenCache.clear();}
}
