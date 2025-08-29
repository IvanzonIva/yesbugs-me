package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.UpdateProfileRequest;
import Ivancompany.nbanktest.api.dto.response.ProfileUpdateResponse;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {

    public ProfileUpdateResponse updateProfile(String authHeader, UpdateProfileRequest updateRequest) {
        return given()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .body(updateRequest)
                .when()
                .put("/customer/profile")
                .then()
                .statusCode(200)
                .extract()
                .as(ProfileUpdateResponse.class);
    }

    public Response updateProfileRaw(String authHeader, UpdateProfileRequest updateRequest) {
        var request = given()
                .header("Content-Type", "application/json")
                .body(updateRequest);

        if (authHeader != null) {
            request.header("Authorization", authHeader);
        }

        return request
                .when()
                .put("/customer/profile");
    }

    //Получение профиля пользователя
    public UserResponse getProfile(String authHeader) {
        return given()
                .header("Authorization", authHeader)
                .when()
                .get("/customer/profile")
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);
    }
}
