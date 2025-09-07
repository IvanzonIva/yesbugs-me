package Ivancompany.nbanktest.tests.functional.user;

import Ivancompany.nbanktest.api.clients.UserClient;
import Ivancompany.nbanktest.api.dto.request.UpdateProfileRequest;
import Ivancompany.nbanktest.api.dto.response.ProfileUpdateResponse;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.models.Role;
import Ivancompany.nbanktest.core.services.UserTestService;
import Ivancompany.nbanktest.core.utils.DataGenerator;
import Ivancompany.nbanktest.core.utils.UserTestHelper;
import Ivancompany.nbanktest.tests.functional.base.ApiTestBase;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserUpdateTest extends ApiTestBase {

    // Константы для сообщений
    private static final String PROFILE_UPDATED_SUCCESS_MESSAGE = "Profile updated successfully";
    private static final String UNAUTHORIZED_MESSAGE = "";

    // Константы для длин имен
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 15;

    private final UserClient userClient = new UserClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserTestService userTestService = new UserTestService(userAdminClient);

    private UserTestHelper.UserTestData testUser;
    private String originalUsername;
    private String userAuthHeader;

    @BeforeEach
    void setUp() {
        testUser = UserTestHelper.createUserWithAccount(Role.USER);
        userAuthHeader = testUser.authHeader();

        UserResponse profile = userClient.getProfile(userAuthHeader);
        originalUsername = profile.getUsername();
    }

    @AfterEach
    void tearDown() {
        if (testUser != null) {
            userTestService.safelyDeleteUser(testUser.userId());
        }
    }

    @ParameterizedTest
    @MethodSource("provideNameTestCases")
    void updateProfileWithDifferentNames(String testName, String nameToUpdate, int expectedStatus, boolean shouldSucceed) {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name(nameToUpdate)
                .build();

        if (shouldSucceed) {
            ProfileUpdateResponse response = userClient.updateProfile(userAuthHeader, request);

            assertThat(response.getMessage(), equalTo(PROFILE_UPDATED_SUCCESS_MESSAGE));
            assertThat(response.getCustomer().getName(), equalTo(nameToUpdate));

            UserResponse updatedProfile = userClient.getProfile(userAuthHeader);
            assertThat(updatedProfile.getName(), equalTo(nameToUpdate));
            assertThat(updatedProfile.getUsername(), equalTo(originalUsername));
        } else {
            Response response = userClient.updateProfileRaw(userAuthHeader, request);

            assertThat(response.getStatusCode(), equalTo(expectedStatus));

            // Проверяем, что username не изменился
            UserResponse unchangedProfile = userClient.getProfile(userAuthHeader);
            assertThat(unchangedProfile.getUsername(), equalTo(originalUsername));
        }
    }

    private static Stream<Arguments> provideNameTestCases() {
        return Stream.of(
                // Валидные тесты
                Arguments.of("Valid name", "Alex Smith", HttpStatus.SC_OK, true),
                Arguments.of("Valid name", "Maria Petrova", HttpStatus.SC_OK, true),
                Arguments.of("Valid name", "John Doe", HttpStatus.SC_OK, true),
                Arguments.of("Minimum length", "ABC", HttpStatus.SC_OK, true),
                Arguments.of("Maximum length", "A".repeat(MAX_NAME_LENGTH), HttpStatus.SC_OK, true),

                // Инвалидные тесты
                Arguments.of("Null value", null, HttpStatus.SC_BAD_REQUEST, false),
                Arguments.of("Empty string", "", HttpStatus.SC_BAD_REQUEST, false),
                Arguments.of("Only spaces", "   ", HttpStatus.SC_BAD_REQUEST, false),
                Arguments.of("Too short", "A", HttpStatus.SC_BAD_REQUEST, false),
                Arguments.of("Too short", "AB", HttpStatus.SC_BAD_REQUEST, false),
                Arguments.of("Too long", "A".repeat(MAX_NAME_LENGTH + 1), HttpStatus.SC_BAD_REQUEST, false)
        );
    }

    @Test
    void userCannotUpdateProfileWithoutAuth() {
        String newName = DataGenerator.generateName();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name(newName)
                .build();

        Response response = userClient.updateProfileRaw(null, request);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat(response.getBody().asString(), equalTo(UNAUTHORIZED_MESSAGE));

        // Проверяем состояние окружения: username не изменился
        UserResponse profile = userClient.getProfile(userAuthHeader);
        assertThat(profile.getUsername(), equalTo(originalUsername));
    }
}