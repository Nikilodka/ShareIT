package org.example.shareit.User.UserValidators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthdayDateValidator.class)
public @interface BirthdayDateRange {
    String message() default "Дата рождения должна быть в диапазоне от {minDate} до {maxDate}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String minDate() default "1920-01-01";
    String maxDate() default "2020-01-01";
}
