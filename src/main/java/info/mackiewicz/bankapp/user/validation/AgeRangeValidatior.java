package info.mackiewicz.bankapp.user.validation;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link AgeRange} annotation.
 * Verifies that a person's age is at least 18 years based on their date of birth.
 * This validator is thread-safe as it maintains no state between validations.
 */
public class AgeRangeValidatior implements ConstraintValidator<AgeRange, LocalDate> {

    /**
     * Initializes the validator.
     * Empty implementation as no initialization is needed.
     *
     * @param constraintAnnotation the annotation instance
     */
    @Override
    public void initialize(AgeRange constraintAnnotation) {
    }

    /**
     * Validates if a person is at least 18 years old based on their date of birth.
     * Calculates the period between the birth date and current date to determine age.
     *
     * @param dateOfBirth the date of birth to validate, may be null
     * @param context constraint validation context
     * @return true if the person is 18 or older, false if underage or date is null
     * @see Period#between(LocalDate, LocalDate)
     */
    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return false;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18 &&
               Period.between(dateOfBirth, LocalDate.now()).getYears() <= 120;
    }
}