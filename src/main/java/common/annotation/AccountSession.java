package common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccountSession {
    int value() default 1; // количество аккаунтов (по умолчанию 1)
    int forUser() default 1; // для какого пользователя создавать аккаунты (индекс)
}