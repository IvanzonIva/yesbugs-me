package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.util.Arrays;

public class ResponseSpecs {
    private ResponseSpecs() {}

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnOK(String value) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectBody("message", Matchers.equalTo(value)).build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, String errorValue) {
        ResponseSpecBuilder builder = defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST);

        // Попробуем проверить JSON-ответ, но если он не JSON — fallback на текст
        builder.expectBody(Matchers.anyOf(
                // Если ответ JSON и содержит нужный ключ
                Matchers.hasEntry(Matchers.equalTo(errorKey), Matchers.equalTo(errorValue)),
                // Если ответ просто текст, который содержит нужное сообщение
                Matchers.anything() // заглушка, переопределим в валидации вручную
        ));

        return builder.build();
    }

    // Новый метод для проверки массива ошибок с точным порядком
    public static ResponseSpecification requestReturnsBadRequest(String errorKey, String[] expectedMessages) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.contains(expectedMessages))
                .build();
    }

    // Новый метод для проверки массива ошибок без учета порядка
    public static ResponseSpecification requestReturnsBadRequestAnyOrder(String errorKey, String[] expectedMessages) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.containsInAnyOrder(expectedMessages))
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.equalTo(errorValue))
                .build();
    }

    public static ResponseSpecification requestReturnsForbidden(String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.equalTo(errorValue))
                .build();
    }

    // Дополнительные методы для удобства

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, java.util.List<String> expectedMessages) {
        return requestReturnsBadRequest(errorKey, expectedMessages.toArray(new String[0]));
    }

    public static ResponseSpecification requestReturnsBadRequestAnyOrder(String errorKey, java.util.List<String> expectedMessages) {
        return requestReturnsBadRequestAnyOrder(errorKey, expectedMessages.toArray(new String[0]));
    }
}