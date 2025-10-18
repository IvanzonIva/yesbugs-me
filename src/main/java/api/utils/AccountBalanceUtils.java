package api.utils;

import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.math.BigDecimal;
import java.util.Arrays;

public class AccountBalanceUtils {

    private static final BigDecimal MAX_PER_DEPOSIT = new BigDecimal("5000.00");

    /**
     * Получает баланс конкретного аккаунта по ID.
     */
    public static BigDecimal getBalanceForAccount(String username, String password, long accountId) {
        DepositResponse[] accounts = new CrudRequesters(
                RequestSpecs.authAsUser(username, password),
                Endpoint.GET_ACCOUNT,
                ResponseSpecs.requestReturnsOK()
        )
                .get()
                .extract()
                .as(DepositResponse[].class);

        return Arrays.stream(accounts)
                .filter(acc -> acc.getId() == accountId)
                .findFirst()
                .map(DepositResponse::getBalance)
                .orElseThrow(() -> new RuntimeException("Account with id " + accountId + " not found"));
    }

    /**
     * Пополняет счёт, разбивая сумму на части, если она превышает лимит на одно пополнение (5000).
     */
    public static void depositEnough(CreateUserRequest user, long accountId, BigDecimal amount) {
        BigDecimal remaining = amount;

        while (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal depositAmount = remaining.min(MAX_PER_DEPOSIT);
            DepositRequest deposit = new DepositRequest(accountId, depositAmount);

            try {
                UserSteps.Deposit(user, deposit);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Ошибка при пополнении счёта пользователя %s на сумму %s: %s",
                        user.getUsername(), depositAmount.toPlainString(), e.getMessage()
                ));
            }

            remaining = remaining.subtract(depositAmount);
        }
    }
}
