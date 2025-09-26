package api.utils;

import api.models.CreateUserRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requests.CrudRequesters;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.requests.steps.UserSteps;

import java.util.Arrays;

public class AccountBalanceUtils {

    public static double getBalanceForAccount(String username, String password, long accountId) {
        DepositResponse[] accounts = new CrudRequesters(
                RequestSpecs.authAsUser(username, password),
                Endpoint.GET_ACCOUNT,
                ResponseSpecs.requestReturnsOK()
        ).get()
                .extract()
                .as(DepositResponse[].class);

        return Arrays.stream(accounts)
                .filter(acc -> acc.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account with id " + accountId + " not found"))
                .getBalance();
    }

    // Новый метод для пополнения не более 5000
    public static void depositEnough(CreateUserRequest user, long accountId, double amount) {
        double maxPerDeposit = 5000.0;
        double remaining = amount;

        while (remaining > 0) {
            double depositAmount = Math.min(remaining, maxPerDeposit);
            DepositRequest deposit = new DepositRequest(accountId, depositAmount);
            UserSteps.Deposit(user, deposit);
            remaining -= depositAmount;
        }
    }
}
