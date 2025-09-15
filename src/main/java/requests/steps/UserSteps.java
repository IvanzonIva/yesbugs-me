package requests.steps;

import io.restassured.response.ValidatableResponse;
import models.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requests.CrudRequesters;
import requests.skelethon.requests.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import io.restassured.specification.ResponseSpecification;

public class UserSteps {


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
        return new CrudRequesters(
                RequestSpecs.depositAsAuthUser(createdUserModel.getUsername(), createdUserModel.getPassword()),
                Endpoint.DEPOSIT,
                responseSpec
        ).post(depositRequest).extract().as(DepositResponse.class);
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