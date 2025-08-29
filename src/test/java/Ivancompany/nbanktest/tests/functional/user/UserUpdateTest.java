package Ivancompany.nbanktest.tests.functional.user;

import Ivancompany.nbanktest.api.clients.UserClient;
import Ivancompany.nbanktest.api.dto.request.UpdateProfileRequest;
import Ivancompany.nbanktest.api.dto.response.ProfileUpdateResponse;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.tests.functional.base.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserUpdateTest extends BaseTest {

    private final UserClient userClient = new UserClient();

    @Test
    void shouldUpdateNameSuccessfully_IvanPavlov() {
        String newName = "Ivan Pavlov";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        ProfileUpdateResponse response = userClient.updateProfile(userAuthHeader, request);

        assertThat(response, notNullValue());
        assertThat(response.getMessage(), equalTo("Profile updated successfully"));
        assertThat(response.getCustomer().getName(), equalTo(newName));

        UserResponse profile = userClient.getProfile(userAuthHeader);
        assertThat(profile.getName(), equalTo(newName));
    }

    @Test
    void shouldUpdateNameSuccessfully_JohnDoe() {
        String newName = "John Doe";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        ProfileUpdateResponse response = userClient.updateProfile(userAuthHeader, request);

        assertThat(response.getMessage(), equalTo("Profile updated successfully"));
        assertThat(response.getCustomer().getName(), equalTo(newName));

        UserResponse profile = userClient.getProfile(userAuthHeader);
        assertThat(profile.getName(), equalTo(newName));
    }

    @Test
    void shouldUpdateNameSuccessfully_TwoLetters() {
        String newName = "I P";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        ProfileUpdateResponse response = userClient.updateProfile(userAuthHeader, request);

        assertThat(response.getMessage(), equalTo("Profile updated successfully"));
        assertThat(response.getCustomer().getName(), equalTo(newName));
    }

    @Test
    void shouldFailUpdateName_SingleWord() {
        String newName = "Ivan";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        Response response = userClient.updateProfileRaw(userAuthHeader, request);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Name must contain two words with letters only"));
    }

    @Test
    void shouldFailUpdateName_WithNumbers() {
        String newName = "Ivan123 Pavlov";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        Response response = userClient.updateProfileRaw(userAuthHeader, request);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Name must contain two words with letters only"));
    }

    @Test
    void shouldFailUpdateName_WithSpecialChars() {
        String newName = "Ivan Pavlov@";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        Response response = userClient.updateProfileRaw(userAuthHeader, request);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Name must contain two words with letters only"));
    }

    @Test
    void shouldFailUpdateName_Empty() {
        String newName = "";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        Response response = userClient.updateProfileRaw(userAuthHeader, request);

        assertThat(response.getStatusCode(), equalTo(400));
        assertThat(response.getBody().asString(), equalTo("Name must contain two words with letters only"));
    }

    @Test
    void shouldFailUpdateName_Unauthorized() {
        String newName = "Pavl Name";
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(newName).build();

        Response response = userClient.updateProfileRaw(null, request);

        assertThat(response.getStatusCode(), equalTo(401));
        assertThat(response.getBody().asString(), equalTo(""));
    }
}
