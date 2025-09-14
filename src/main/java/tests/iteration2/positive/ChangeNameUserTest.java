package tests.iteration2.positive;

import models.ChangeNameRequest;
import models.ChangeNameResponse;
import models.CreateUserRequest;
import models.GetUserResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import tests.iteration1.BaseTest;
import utils.TestDataFactory;

public class ChangeNameUserTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Ivan Pavlov",
            "John Doe",
            "I P"
    })
    public void authUserCanUpdateOwnName(String newUserName) {

        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);
        ChangeNameRequest changeName = TestDataFactory.changeNameModel(newUserName);
        ChangeNameResponse responseModel = UserSteps.changeName(createdUser, changeName);
        GetUserResponse responseModelAfter = UserSteps.getUser(createdUser);

        softly.assertThat(responseModel.getCustomer().getName())
                .as("Имя в ответе на изменение должно совпадать с новым")
                .isEqualTo(newUserName);
        softly.assertThat(responseModelAfter.getName())
                .as("Имя в getUser должно совпадать с новым")
                .isEqualTo(newUserName);
        softly.assertThat(responseModel.getMessage())
                .as("Сообщение в ответе должно быть успешным")
                .isEqualTo("Profile updated successfully");
    }
}
