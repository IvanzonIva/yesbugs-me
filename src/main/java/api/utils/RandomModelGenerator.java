package api.utils;

import com.github.curiousoddman.rgxgen.RgxGen;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class RandomModelGenerator {

    private static final Random random = new Random();

    @SuppressWarnings("unchecked")
    public static <T> T generate(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = generateFieldValue(field);
                field.set(instance, value);
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации модели: " + clazz.getSimpleName(), e);
        }
    }

    private static Object generateFieldValue(Field field) {
        if (field.isAnnotationPresent(GeneratingRule.class)) {
            GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
            String generatedValue = new RgxGen(rule.regex()).generate();
            return convertToType(generatedValue, field.getType());
        } else {
            return generateRandomValue(field.getType());
        }
    }

    private static Object generateRandomValue(Class<?> type) {
        if (type.equals(String.class)) {
            return UUID.randomUUID().toString().substring(0, 8);
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            return random.nextInt(1000);
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            return random.nextLong();
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return roundDouble(random.nextDouble() * 10000); // чтобы не давал слишком большие значения
        } else if (type.equals(BigDecimal.class)) {
            return getRandomBigDecimal(0.01, 5000.00);
        } else if (type.equals(LocalDate.class)) {
            return LocalDate.now().minusDays(random.nextInt(365));
        } else if (type.isEnum()) {
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        }
        return null;
    }

    private static Object convertToType(String value, Class<?> targetType) {
        try {
            if (targetType.equals(String.class)) {
                return value;
            } else if (targetType.isEnum()) {
                Object[] enumValues = targetType.getEnumConstants();
                for (Object enumValue : enumValues) {
                    if (enumValue.toString().equals(value)) {
                        return enumValue;
                    }
                }
                return enumValues[random.nextInt(enumValues.length)];
            } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
                return Integer.parseInt(value);
            } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
                return Long.parseLong(value);
            } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
                return Boolean.parseBoolean(value);
            } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
                return Double.parseDouble(value);
            } else if (targetType.equals(BigDecimal.class)) {
                return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (NumberFormatException e) {
            // fallback — если regex выдал нечисловое
            return generateRandomValue(targetType);
        }

        return null;
    }

    private static double roundDouble(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static BigDecimal getRandomBigDecimal(double min, double max) {
        double randomValue = random.nextDouble() * (max - min) + min;
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
    }
}
