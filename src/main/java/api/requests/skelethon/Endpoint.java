package api.requests.skelethon;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),
    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    DEPOSIT (
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),

    TRANSFER (
            "/accounts/transfer",
            TransferRequest.class,
            TransferResponse.class

    ),

    CHANGE_NAME (
            "/customer/profile",
            ChangeNameRequest.class,
            ChangeNameResponse.class
    ),

    GET_USER (
            "/customer/profile",
            BaseModel.class,
            GetUserResponse.class
    ),

    GET_ACCOUNT (
            "/customer/accounts",
            BaseModel.class,
            DepositResponse.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}

