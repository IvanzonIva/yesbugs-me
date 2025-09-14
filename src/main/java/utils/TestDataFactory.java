package utils;

import models.ChangeNameRequest;
import models.CreateUserRequest;
import models.DepositRequest;
import models.TransferRequest;

public class TestDataFactory {

    public static CreateUserRequest createUserModel() {
        return RandomModelGenerator.generate(CreateUserRequest.class);
    }

    public static DepositRequest createDepositModel(long accountId, double balance) {
        return DepositRequest.builder()
                .id(accountId)
                .balance(balance).build();
    }

    public static TransferRequest createTransferModel(long accountIdOne, long accountIdTwo, double balance) {
        return TransferRequest.builder()
                .senderAccountId(accountIdOne)
                .receiverAccountId(accountIdTwo)
                .amount(balance).build();
    }

    public static ChangeNameRequest changeNameModel(String newUserName) {
        return ChangeNameRequest.builder()
                .name(newUserName).build();
    }
}
