package tests.iteration1;

import models.CreateUserRequest;
import org.junit.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequesters;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUser();

        // Создание аккаунта (счета)
        new CrudRequesters(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);

    }
}