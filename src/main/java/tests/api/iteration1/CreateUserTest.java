package tests.api.iteration1;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.UserRole;
import api.models.comparison.ModelAssertions;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.utils.RandomModelGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {
    @Test
    public void adminCanCreateUserWithCorrectData() {
        CreateUserRequest createUserRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = new ValidatedCrudRequester<CreateUserResponse>
                (RequestSpecs.adminSpec(),
                        Endpoint.ADMIN_USER,
                        ResponseSpecs.entityWasCreated())
                .post(createUserRequest);


        ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();
    }

    public static Stream<Arguments> userInvalidData() {
        return Stream.of(
                // пользователь не прошел проверку
                Arguments.of("", "Password33$", UserRole.USER, "username", "Username cannot be blank"),
                Arguments.of("ab", "Password33$", UserRole.USER, "username", "Username must be between 3 and 15 characters"),
                Arguments.of("abc%", "Password33$", UserRole.USER, "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("abc$", "Password33$", UserRole.USER, "username", "Username must contain only letters, digits, dashes, underscores, and dots")
        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCanNotCreateUserWithInvalidData(String username, String password, UserRole role, String errorKey, String errorValue) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new CrudRequesters(RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .post(createUserRequest);
    }
}
