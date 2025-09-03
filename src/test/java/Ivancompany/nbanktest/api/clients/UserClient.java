package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.UpdateProfileRequest;
import Ivancompany.nbanktest.api.dto.response.ProfileUpdateResponse;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import io.restassured.response.Response;

public class UserClient extends BaseClient {

    public ProfileUpdateResponse updateProfile(String authHeader, UpdateProfileRequest request) {
        return request(authHeader)
                .body(request)
                .when()
                .put("/profile")
                .then()
                .extract()
                .as(ProfileUpdateResponse.class);
    }

    public Response updateProfileRaw(String authHeader, UpdateProfileRequest request) {
        return (authHeader == null ? requestWithoutAuth() : request(authHeader))
                .body(request)
                .when()
                .put("/profile")
                .then()
                .extract()
                .response();
    }

    public UserResponse getProfile(String authHeader) {
        return request(authHeader)
                .when()
                .get("/profile")
                .then()
                .extract()
                .as(UserResponse.class);
    }
}
