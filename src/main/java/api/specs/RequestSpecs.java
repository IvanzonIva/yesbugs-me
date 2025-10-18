package api.specs;

import api.configs.Config;
import api.models.LoginUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // ✅ Потокобезопасная альтернатива HashMap

public class RequestSpecs {

    // ✅ Используем ConcurrentHashMap, чтобы избежать коллизий при параллельных тестах
    private static final Map<String, String> authHeaders = new ConcurrentHashMap<>(
            Map.of("admin", "Basic YWRtaW46YWRtaW4=")
    );

    private RequestSpecs() {
        // Utility class
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter(),
                        new AllureRestAssured()))
                .setBaseUri(Config.getProperty("apiBaseUrl") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return defaultRequestBuilder()
                .addHeader("Authorization", getUserAuthHeader(username, password))
                .build();
    }

    // ✅ depositAsAuthUser просто обертка для читаемости
    public static RequestSpecification depositAsAuthUser(String username, String password) {
        return authAsUser(username, password);
    }

    /**
     * Возвращает Authorization header для пользователя.
     * Если токен уже есть в кеше — берем оттуда.
     * Если нет — делаем запрос /login и сохраняем.
     */
    public static String getUserAuthHeader(String username, String password) {
        return authHeaders.computeIfAbsent(username, user -> {
            var response = new CrudRequesters(
                    unauthSpec(),
                    Endpoint.LOGIN,
                    ResponseSpecs.requestReturnsOK()
            ).post(LoginUserRequest.builder()
                    .username(username)
                    .password(password)
                    .build());

            return response.extract().header("Authorization");
        });
    }
}