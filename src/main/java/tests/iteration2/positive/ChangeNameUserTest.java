package tests.iteration2.positive;

import models.ChangeNameRequest;
import models.ChangeNameResponse;
import models.CreateUserRequest;
import models.GetUserResponse;
import models.comparison.ModelAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import tests.iteration1.BaseTest;
import utils.TestDataFactory;

public class ChangeNameUserTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = { "Ivan Pavlov", "John Doe", "I P" })
    public void authUserCanUpdateOwnName(String newUserName) {
        // создаём пользователя
        CreateUserRequest createdUser = TestDataFactory.createUserModel();
        AdminSteps.createUser(createdUser);

        // делаем запрос на смену имени
        ChangeNameRequest changeName = TestDataFactory.changeNameModel(newUserName);
        ChangeNameResponse responseModel = UserSteps.changeName(createdUser, changeName);

        // получаем юзера после изменения
        GetUserResponse responseModelAfter = UserSteps.getUser(createdUser);

        // сравнение моделей через ModelAssertions
        ModelAssertions.assertThatModels(changeName, responseModel.getCustomer())
                .as("Имя в ответе на изменение должно совпадать с новым")
                .match();

        ModelAssertions.assertThatModels(changeName, responseModelAfter)
                .as("Имя в getUser должно совпадать с новым")
                .match();

        // дополнительная проверка по сообщению
        softly.assertThat(responseModel.getMessage())
                .as("Сообщение в ответе должно быть успешным")
                .isEqualTo("Profile updated successfully");
    }
}
