package Ivancompany.nbanktest.core.utils;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker = new Faker();

    public static String generateValidUsername() {
        String base = faker.name().username();
        String username = base.replaceAll("[^a-zA-Z0-9._-]", "");
        if (username.length() < 3) username += "123";
        return username.substring(0, Math.min(username.length(), 15));
    }

    public static String generateValidPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&+=";

        StringBuilder password = new StringBuilder();
        password.append(getRandomChar(upper))
                .append(getRandomChar(lower))
                .append(getRandomChar(digits))
                .append(getRandomChar(special));

        String allChars = upper + lower + digits + special;
        while (password.length() < 12) {
            password.append(getRandomChar(allChars));
        }

        return password.toString();
    }

    private static char getRandomChar(String charSet) {
        return charSet.charAt(faker.random().nextInt(charSet.length()));
    }

    public static String generateRole() {
        return faker.bool().bool() ? "ADMIN" : "USER";
    }

    public static String generateName() {
        return faker.name().fullName();
    }

    // ====== СУММЫ ДЛЯ ТЕСТОВ ======
    public static Double generateAmount() {
        return faker.number().randomDouble(2, 10, 1000);
    }

    public static Double validTransferAmount() {
        return faker.number().randomDouble(2, 1, 500); // всегда валидное значение
    }

    public static Double overLimitTransferAmount() {
        return 10001.0; // заведомо больше лимита
    }

    public static Double zeroAmount() {
        return 0.0;
    }

    public static Double negativeAmount() {
        return -faker.number().randomDouble(2, 1, 500); // случайное отрицательное
    }
}
