package Ivancompany.nbanktest.tests.functional.user;

import Ivancompany.nbanktest.api.clients.UserClient;
import Ivancompany.nbanktest.api.dto.request.UpdateProfileRequest;
import Ivancompany.nbanktest.api.dto.response.ProfileUpdateResponse;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.utils.DataGenerator;
import Ivancompany.nbanktest.tests.functional.base.SingleUserTestBase;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserUpdateTest extends SingleUserTestBase {

    private final UserClient userClient = new UserClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String originalUsername;

    @BeforeEach
    void saveOriginalProfile() {
        UserResponse profile = userClient.getProfile(userAuthHeader);
        originalUsername = profile.getUsername();
        System.out.println("Original username: " + originalUsername);
    }

    @ParameterizedTest(name = "Name: {0} → Expected: {1}")
    @MethodSource("provideNameTestCases")
    void updateProfileWithDifferentNames(String testName, String nameToUpdate, int expectedStatus, boolean shouldSucceed) throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name(nameToUpdate)
                .build();

        // Логируем запрос
        System.out.println("=== Test case: " + testName + " ===");
        System.out.println("Request JSON: " + objectMapper.writeValueAsString(request));

        if (shouldSucceed) {
            ProfileUpdateResponse response = userClient.updateProfile(userAuthHeader, request);

            // Логируем ответ
            System.out.println("Response JSON: " + objectMapper.writeValueAsString(response));

            assertThat(response.getMessage(), equalTo("Profile updated successfully"));
            assertThat(response.getCustomer().getName(), equalTo(nameToUpdate));

            UserResponse updatedProfile = userClient.getProfile(userAuthHeader);
            System.out.println("Updated profile: " + objectMapper.writeValueAsString(updatedProfile));

            assertThat(updatedProfile.getName(), equalTo(nameToUpdate));
            assertThat(updatedProfile.getUsername(), equalTo(originalUsername));
        } else {
            Response response = userClient.updateProfileRaw(userAuthHeader, request);

            // Логируем ответ
            System.out.println("Response code: " + response.getStatusCode());
            System.out.println("Response body: " + response.asString());

            assertThat(response.getStatusCode(), equalTo(expectedStatus));

            // Проверяем, что username не изменился
            UserResponse unchangedProfile = userClient.getProfile(userAuthHeader);
            System.out.println("Unchanged profile: " + objectMapper.writeValueAsString(unchangedProfile));

            assertThat(unchangedProfile.getUsername(), equalTo(originalUsername));
        }
    }

    private static Stream<Arguments> provideNameTestCases() {
        return Stream.of(
                // Валидные тесты
                Arguments.of("Valid name", "Alex Smith", 200, true),
                Arguments.of("Valid name", "Maria Petrova", 200, true),
                Arguments.of("Valid name", "John Doe", 200, true),
                Arguments.of("Minimum length (3)", "ABC", 200, true),
                Arguments.of("Maximum length (15)", "A".repeat(15), 200, true),

                // Инвалидные тесты
                Arguments.of("Null value", null, 400, false),
                Arguments.of("Empty string", "", 400, false),
                Arguments.of("Only spaces", "   ", 400, false),
                Arguments.of("Too short (1)", "A", 400, false),
                Arguments.of("Too short (2)", "AB", 400, false),
                Arguments.of("Too long (16)", "A".repeat(16), 400, false)
        );
    }

    @Test
    void userCannotUpdateProfileWithoutAuth() throws Exception {
        String newName = DataGenerator.generateName();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name(newName)
                .build();

        System.out.println("Request (unauthenticated): " + objectMapper.writeValueAsString(request));

        Response response = userClient.updateProfileRaw(null, request);

        System.out.println("Response code: " + response.getStatusCode());
        System.out.println("Response body: " + response.asString());

        assertThat(response.getStatusCode(), equalTo(401));

        // Проверяем состояние окружения: username не изменился
        UserResponse profile = userClient.getProfile(userAuthHeader);
        System.out.println("Profile after unauthenticated attempt: " + objectMapper.writeValueAsString(profile));

        assertThat(profile.getUsername(), equalTo(originalUsername));
    }
}
