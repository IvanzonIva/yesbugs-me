package tests.iteration2.negative;

import models.ChangeNameRequest;
import models.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequesters;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import tests.iteration1.BaseTest;
import utils.TestDataFactory;

import java.util.stream.Stream;

public class ChangeNameUserNegativeTest extends BaseTest {

    private CreateUserRequest createdUser;

    @BeforeEach
    public void setUp() {
        createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);
    }

    public static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of("   ", "name", "Username cannot be blank"),
                Arguments.of("ab", "name", "Username must be between 3 and 15 characters"),
                Arguments.of("thisusernameiswaytoolong", "name", "Username must be between 3 and 15 characters"),
                Arguments.of("user!@#", "name", "Invalid characters in username"),
                Arguments.of("user name", "name", "Username must not contain spaces")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void authUserCanNotUpdateOwnNameWithInvalidData(String invalidName, String errorKey, String errorValue) {
        ChangeNameRequest changeName = TestDataFactory.changeNameModel(invalidName);

        new CrudRequesters(
                RequestSpecs.authAsUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.CHANGE_NAME,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .update(changeName);
    }
}