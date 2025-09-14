package requests.steps;

import models.CreateUserRequest;
import models.CreateUserResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import utils.RandomModelGenerator;

public class AdminSteps {

    // Существующий метод без параметров
    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        return createUser(userRequest);
    }

    // Новый перегруженный метод с параметром
    public static CreateUserRequest createUser(CreateUserRequest userRequest) {
        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }
}
