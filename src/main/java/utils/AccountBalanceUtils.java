package utils;

import models.DepositResponse;

import requests.skelethon.Endpoint;

import requests.skelethon.requests.CrudRequesters;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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
}
