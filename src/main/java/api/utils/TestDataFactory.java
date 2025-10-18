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

    // Пополнение с конкретной суммой (BigDecimal)
    public static DepositRequest createDepositModel(long accountId, BigDecimal balance) {
        return DepositRequest.builder()
                .id(accountId)
                .balance(balance.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    // Пополнение со случайной суммой
    public static DepositRequest createDepositModel(long accountId) {
        return DepositRequest.builder()
                .id(accountId)
                .balance(getRandomDepositAmount())
                .build();
    }

    // Генерация случайных сумм
    public static BigDecimal getRandomDepositAmount() {
        return getRandomAmount(new BigDecimal("0.10"), new BigDecimal("5000.00"));
    }

    // Генерация BigDecimal-значений в диапазоне (обновленная версия)
    public static BigDecimal getRandomAmount(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) >= 0) {
            throw new IllegalArgumentException("min must be less than max");
        }

        BigDecimal range = max.subtract(min);
        // Генерируем случайное число от 0 до 1 как BigDecimal
        BigDecimal randomFactor = BigDecimal.valueOf(random.nextDouble());
        // Вычисляем случайную сумму в диапазоне
        BigDecimal randomAmount = min.add(range.multiply(randomFactor));

        return randomAmount.setScale(2, RoundingMode.HALF_UP);
    }

    // Сохраняем старый метод для обратной совместимости
    public static BigDecimal getRandomAmount(double from, double to) {
        return getRandomAmount(BigDecimal.valueOf(from), BigDecimal.valueOf(to));
    }

    // Перевод с конкретной суммой
    public static TransferRequest createTransferModel(long accountIdOne, long accountIdTwo, BigDecimal amount) {
        return TransferRequest.builder()
                .senderAccountId(accountIdOne)
                .receiverAccountId(accountIdTwo)
                .amount(amount.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    // Перевод со случайной суммой
    public static TransferRequest createTransferModel(long accountIdOne, long accountIdTwo) {
        return TransferRequest.builder()
                .senderAccountId(accountIdOne)
                .receiverAccountId(accountIdTwo)
                .amount(getRandomDepositAmount())
                .build();
    }

    public static ChangeNameRequest changeNameModel(String newUserName) {
        return ChangeNameRequest.builder()
                .name(newUserName)
                .build();
    }
}