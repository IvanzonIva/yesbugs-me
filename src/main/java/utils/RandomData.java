package utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomData {
    private RandomData() {}

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        // гарантируем минимум один символ каждого типа
        String upper = RandomStringUtils.randomAlphabetic(3).toUpperCase();      // 3 заглавные
        String lower = RandomStringUtils.randomAlphabetic(5).toLowerCase();       // 5 строчных
        String digit = RandomStringUtils.randomNumeric(1);                        // 1 цифра
        String special = "$#!";                                                   // 3 спецсимвола

        // объединяем все части
        String rawPassword = upper + lower + digit + special;

        // перемешиваем символы
        List<Character> chars = rawPassword.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(chars);

        return chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
