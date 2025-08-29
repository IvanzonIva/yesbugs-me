package Ivancompany.nbanktest.core.config;

import java.util.ResourceBundle;

public class TestConfig {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("config");

    public static final String BASE_URL = BUNDLE.getString("base.url");
    public static final String ADMIN_USERNAME = BUNDLE.getString("admin.username");
    public static final String ADMIN_PASSWORD = BUNDLE.getString("admin.password");
    public static final String ADMIN_AUTH = "Basic " + java.util.Base64.getEncoder()
            .encodeToString((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes());
}
