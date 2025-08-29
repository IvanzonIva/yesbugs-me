package Ivancompany.nbanktest.core.utils;

import com.github.javafaker.Faker;

public class DataGenerator {
    private static final Faker faker = new Faker();

    public static String generateValidUsername() {
        String base = faker.name().username(); // например: kate1998
        // Оставляем только допустимые символы
        String username = base.replaceAll("[^a-zA-Z0-9._-]", "");
        // Ограничиваем длину 15 символами, минимум 3
        if (username.length() < 3) username = username + "123";
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
        while (password.length() < 12) { // делаем чуть длиннее минимума
            password.append(getRandomChar(allChars));
        }

        return password.toString();
    }

    private static char getRandomChar(String charSet) {
        return charSet.charAt(faker.random().nextInt(charSet.length()));
    }

    //Генерация роли пользователя
    public static String generateRole() {
        return faker.bool().bool() ? "ADMIN" : "USER";
    }

    // Генерация случайного полного имени

    public static String generateName() {
        return faker.name().fullName(); // например: "Kate Johnson"
    }

    //Генерация суммы
    public static Double generateAmount() {
        return faker.number().randomDouble(2, 10, 1000);
    }
}
