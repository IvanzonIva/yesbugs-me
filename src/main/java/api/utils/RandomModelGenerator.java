package api.utils;

import com.github.curiousoddman.rgxgen.RgxGen;
import java.lang.reflect.Field;
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
            return random.nextDouble();
        } else if (type.equals(LocalDate.class)) {
            return LocalDate.now().minusDays(random.nextInt(365));
        } else if (type.isEnum()) {
            // Генерация случайного значения для enum
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        }
        return null;
    }

    private static Object convertToType(String value, Class<?> targetType) {
        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.isEnum()) {
            // Преобразование строки в enum
            Object[] enumValues = targetType.getEnumConstants();
            for (Object enumValue : enumValues) {
                if (enumValue.toString().equals(value)) {
                    return enumValue;
                }
            }
            // Если не нашли точное совпадение, возвращаем случайное значение enum
            return enumValues[random.nextInt(enumValues.length)];
        } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return random.nextInt(1000);
            }
        } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return random.nextLong();
            }
        } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return random.nextDouble();
            }
        }

        // Для неподдерживаемых типов возвращаем null
        return null;
    }
}