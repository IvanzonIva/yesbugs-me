package api.requests.steps;

import api.models.*;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.requests.skelethon.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;

import java.util.List;

public class UserSteps {

    private String username;
    private String password;

    public UserSteps(String username, String password){
        this.username = username;
        this.password = password;
    }

    public List<CreateAccountResponse> getAllAccounts() {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.GET_ACCOUNT,
                ResponseSpecs.requestReturnsOK()).getAll(CreateAccountResponse[].class);
    }


    public static ValidatableResponse createAccount(CreateUserRequest createUserRequestModel) {
        return new CrudRequesters(
                RequestSpecs.authAsUser(createUserRequestModel.getUsername(), createUserRequestModel.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public static DepositResponse Deposit(CreateUserRequest createdUserModel,
                                          DepositRequest depositRequest,
                                          ResponseSpecification responseSpec) {

        ValidatableResponse response = new CrudRequesters(
                RequestSpecs.depositAsAuthUser(createdUserModel.getUsername(), createdUserModel.getPassword()),
                Endpoint.DEPOSIT,
                responseSpec
        ).post(depositRequest);

        // Если статус 200 — это успешный запрос, тогда можно парсить JSON
        if (response.extract().statusCode() == 200) {
            return response.extract().as(DepositResponse.class);
        } else {
            // Для ошибок ничего не парсим, просто возвращаем null (или можешь выбросить исключение)
            return null;
        }
    }

    // Перегрузка
    public static DepositResponse Deposit(CreateUserRequest createdUserModel,
                                          DepositRequest depositRequest) {
        return Deposit(createdUserModel, depositRequest, ResponseSpecs.requestReturnsOK());
    }

    public static TransferResponse makeTransfer(CreateUserRequest createdUserModel,
                                                TransferRequest transferRequestModel) {
        return new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(createdUserModel.getUsername(), createdUserModel.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(transferRequestModel);
    }

    public static long getAccountID(ValidatableResponse validatableResponse) {
        return ((Integer) validatableResponse.extract().path("id")).longValue();
    }

    public static ChangeNameResponse changeName(CreateUserRequest createdUser, ChangeNameRequest newUserName) {
        return new ValidatedCrudRequester<ChangeNameResponse>(
                RequestSpecs.authAsUser(createdUser.getUsername(),
                        createdUser.getPassword()),
                Endpoint.CHANGE_NAME,
                ResponseSpecs.requestReturnOK("Profile updated successfully"))
                .update(newUserName);
    }

    public static GetUserResponse getUser(CreateUserRequest createdUser) {
        return (GetUserResponse) new ValidatedCrudRequester<GetUserResponse>(
                RequestSpecs.authAsUser(createdUser.getUsername(), createdUser.getPassword()),
                Endpoint.GET_USER,
                ResponseSpecs.requestReturnsOK()).get();
    }

}