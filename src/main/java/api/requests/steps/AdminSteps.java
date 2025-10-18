package api.requests.steps;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.utils.RandomModelGenerator;
import common.helpers.StepLogger;

import java.util.List;

public class AdminSteps {

    // Существующий метод без параметров
    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        return createUser(userRequest);
    }

    // Новый перегруженный метод с параметром
    public static CreateUserRequest createUser(CreateUserRequest userRequest) {
        return StepLogger.log("Создание нового пользователя через API", () -> {
            new ValidatedCrudRequester<CreateUserResponse>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.entityWasCreated())
                    .post(userRequest);

            return userRequest;
        });
    }

    public static List<CreateUserResponse> getAllUsers() {
        return StepLogger.log("Получение списка всех пользователей через API", () -> {
            return new ValidatedCrudRequester<CreateUserResponse>(
                    RequestSpecs.adminSpec(),
                    Endpoint.ADMIN_USER,
                    ResponseSpecs.requestReturnsOK()).getAll(CreateUserResponse[].class);
        });
    }
}