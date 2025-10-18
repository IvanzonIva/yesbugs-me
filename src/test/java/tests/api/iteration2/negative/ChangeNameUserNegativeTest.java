package tests.api.iteration2.negative;

import api.models.ChangeNameRequest;
import api.models.CreateUserRequest;
import api.models.GetUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import tests.api.iteration1.BaseTest;
import api.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.steps.UserSteps;

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
                Arguments.of("user!@#", "name", "Invalid characters in username")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void authUserCanNotUpdateOwnNameWithInvalidData(String invalidName, String errorKey, String errorValue) {
        // Запоминаем текущее имя пользователя
        GetUserResponse beforeChange = UserSteps.getUser(createdUser);
        String oldName = beforeChange.getName();

        // Пытаемся обновить невалидным именем
        ChangeNameRequest changeName = TestDataFactory.changeNameModel(invalidName);
        new CrudRequesters(
                RequestSpecs.authAsUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.CHANGE_NAME,
                ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue))
                .update(changeName);

        // Проверяем, что имя осталось прежним
        GetUserResponse afterChange = UserSteps.getUser(createdUser);
        softly.assertThat(afterChange.getName())
                .as("Имя пользователя не должно обновиться при вводе невалидного значения")
                .isEqualTo(oldName);
    }
}