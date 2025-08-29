package Ivancompany.nbanktest.core.utils;

public class AuthHelper {
    public static String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}