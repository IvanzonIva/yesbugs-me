package api.utils;

import common.helpers.StepLogger;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Принимаем на вход общего ретрая:
 * 1) Что принимаем
 * 2) Условия выхода
 * 3) Количество попыток МАХ
 * 4) Задержка между каждой попыткой
 */

public class RetryUtils {

    public static <T> T retry(
            Supplier<T> action,
            Predicate<T> condition,
            int maxAttempts,
            long delayMillis) {

        T result = null;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            // Логируем попытку
            StepLogger.log(String.format("Попытка %d/%d", attempts, maxAttempts), () -> {});

            result = action.get();

            if (condition.test(result)) {
                StepLogger.log("Успешное завершение", () -> {});
                return result;
            }

            if (attempts < maxAttempts) {
                StepLogger.log(String.format("Неудача, ожидание %d мс перед следующей попыткой", delayMillis), () -> {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", e);
                    }
                });
            }
        }

        throw new RuntimeException(String.format("Повторные попытки завершились неудачей после %d попыток!", maxAttempts));
    }

    // Перегруженный метод с дефолтными значениями
    public static <T> T retry(
            Supplier<T> action,
            Predicate<T> condition,
            int maxAttempts) {
        return retry(action, condition, maxAttempts, 1000);
    }

    // Перегруженный метод с дефолтными значениями (3 попытки, 1 секунда)
    public static <T> T retry(
            Supplier<T> action,
            Predicate<T> condition) {
        return retry(action, condition, 3, 1000);
    }

    // Метод для операций без возвращаемого значения (void)
    public static void retry(
            Runnable action,
            int maxAttempts,
            long delayMillis) {

        int attempts = 0;
        RuntimeException lastException = null;

        while (attempts < maxAttempts) {
            attempts++;
            StepLogger.log(String.format("Попытка %d/%d", attempts, maxAttempts), () -> {});

            try {
                action.run();
                StepLogger.log("Успешное завершение", () -> {});
                return; // Успех - выходим
            } catch (RuntimeException e) {
                lastException = e;
                StepLogger.log(String.format("Неудача: %s", e.getMessage()), () -> {});

                if (attempts < maxAttempts) {
                    StepLogger.log(String.format("Ожидание %d мс перед следующей попыткой", delayMillis), () -> {
                        try {
                            Thread.sleep(delayMillis);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Retry interrupted", ie);
                        }
                    });
                }
            }
        }

        throw new RuntimeException(
                String.format("Повторные попытки завершились неудачей после %d попыток!", maxAttempts),
                lastException
        );
    }
}