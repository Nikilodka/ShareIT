package org.example.shareit.User.UserValidators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.Date;

public class BirthdayDateValidator implements ConstraintValidator<BirthdayDateRange, LocalDate> {
    private LocalDate minDate;
    private LocalDate maxDate;

    @Override
    public void initialize(BirthdayDateRange constraintAnnotation) {
        this.minDate=LocalDate.parse(constraintAnnotation.minDate());
        this.maxDate=LocalDate.parse(constraintAnnotation.maxDate());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value.isAfter(minDate) && value.isBefore(maxDate)) {
            return true;
        }
        return false;
    }
}
