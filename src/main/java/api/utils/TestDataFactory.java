package api.utils;

import api.models.CreateUserRequest;
import api.models.ChangeNameRequest;
import api.models.DepositRequest;
import api.models.TransferRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class TestDataFactory {
    private static final Random random = new Random();

    public static CreateUserRequest createUserModel() {
        return RandomModelGenerator.generate(CreateUserRequest.class);
    }

    // Пополение с конкретной суммой
    public static DepositRequest createDepositModel(long accountId, double balance) {
        return DepositRequest.builder()
                .id(accountId)
                .balance(balance).build();
    }

    // Пополнение со случайной суммой
    public static DepositRequest createDepositModel(long accountId) {
        return DepositRequest.builder()
                .id(accountId)
                .balance(getRandomDepositAmount().doubleValue()).build();
    }

    // Генерации случайных сумм
    public static BigDecimal getRandomDepositAmount() {
        return getRandomAmount(0.1, 5000);
    }

    public static BigDecimal getRandomAmount(double from, double to) {
        double randomValue = random.nextDouble() * (to - from) + from;
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
    }

    public static TransferRequest createTransferModel(long accountIdOne, long accountIdTwo, double balance) {
        return TransferRequest.builder()
                .senderAccountId(accountIdOne)
                .receiverAccountId(accountIdTwo)
                .amount(balance).build();
    }

    // Перевода со случайной суммой
    public static TransferRequest createTransferModel(long accountIdOne, long accountIdTwo) {
        return TransferRequest.builder()
                .senderAccountId(accountIdOne)
                .receiverAccountId(accountIdTwo)
                .amount(getRandomDepositAmount().doubleValue()).build();
    }

    public static ChangeNameRequest changeNameModel(String newUserName) {
        return ChangeNameRequest.builder()
                .name(newUserName).build();
    }
}