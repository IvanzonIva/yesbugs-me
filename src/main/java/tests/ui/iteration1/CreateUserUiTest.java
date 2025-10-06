package tests.ui.iteration1;

import UI.elements.UserBage;
import UI.pages.AdminPanel;
import UI.pages.BankAlert;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import api.utils.RandomModelGenerator;
import common.annotation.AdminSession;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CreateUserUiTest extends BaseUiTest {

    @Test
@AdminSession // Junit Extension
    public void adminCanCreateUserTest() {
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        UserBage newUserBage = new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .findUserByUsername(newUser.getUsername());

        assertThat(newUserBage)
                .as("UserBage should exist on Dashbord after user creation").isNotNull();

        CreateUserResponse createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();

    }

    @Test
    @AdminSession // Junit Extension
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);
        newUser.setUsername("a");

        assertTrue(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
               .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
               .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        long usersWithSameUsernameAsNewUser = AdminSteps.getAllUsers().stream().filter(user -> user.getUsername()
                .equals(newUser.getUsername())).count();

        assertThat(usersWithSameUsernameAsNewUser).isZero();

    }
}
