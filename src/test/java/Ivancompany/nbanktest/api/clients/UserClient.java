package Ivancompany.nbanktest.api.clients;

import Ivancompany.nbanktest.api.dto.request.UpdateProfileRequest;
import Ivancompany.nbanktest.api.dto.response.ProfileUpdateResponse;
import Ivancompany.nbanktest.api.dto.response.UserResponse;
import Ivancompany.nbanktest.core.specs.RequestSpecs;
import Ivancompany.nbanktest.core.specs.ResponseSpecs;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {

    public ProfileUpdateResponse updateProfile(String authHeader, UpdateProfileRequest request) {
        return given()
                .spec(RequestSpecs.authSpec(authHeader))
                .body(request)
                .when()
                .put("/profile")
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .as(ProfileUpdateResponse.class);
    }

    public Response updateProfileRaw(String authHeader, UpdateProfileRequest request) {
        RequestSpecification spec = (authHeader == null)
                ? RequestSpecs.unauthSpec()
                : RequestSpecs.authSpec(authHeader);

        return given()
                .spec(spec)
                .body(request)
                .when()
                .put("/profile")
                .then()
                .extract()
                .response();
    }

    public UserResponse getProfile(String authHeader) {
        return given()
                .spec(RequestSpecs.authSpec(authHeader))
                .when()
                .get("getProfile")
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .as(UserResponse.class);
    }
}