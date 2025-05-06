package info.mackiewicz.bankapp.shared.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a user's age is at least 18 years old.
 * This annotation can be applied to date fields representing birth dates.
 * The validation is performed by {@link AgeRangeValidatior}.
 *
 * <p>Example usage:
 * <pre>
 * public class User {
 *     &#64;Adult
 *     private LocalDate birthDate;
 * }
 * </pre>
 *
 * @see AgeRangeValidatior
 */
@Constraint(validatedBy = AgeRangeValidatior.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeRange {
    /**
     * Defines the error message when validation fails.
     * @return the error message
     */
    String message() default "User must be at least 18 years old and not older than 120 years old";

    /**
     * Allows validation groups to be specified.
     * Groups enable conditional validation based on context.
     * @return validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Allows passing metadata within the annotation.
     * @return validation payloads
     */
    Class<? extends Payload>[] payload() default {};
}