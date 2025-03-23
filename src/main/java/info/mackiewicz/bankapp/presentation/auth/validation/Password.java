package info.mackiewicz.bankapp.presentation.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;

/**
 * Annotation for validating password fields.
 *
 * <p>This annotation is used to enforce specific password validation rules, including:
 * <ul>
 *   <li>Not blank</li>
 *   <li>Minimum length of 8 characters</li>
 *   <li>At least one digit (0-9)</li>
 *   <li>At least one lowercase letter (a-z)</li>
 *   <li>At least one uppercase letter (A-Z)</li>
 *   <li>At least one special character from the set: "@$!%*?&"</li>
 * </ul>
 *
 * @see ValidationConstants
 */


@NotBlank(message = "Password is required")
@Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, message = "Password must be at least 8 characters long")
@Pattern(
    regexp = ValidationConstants.PASSWORD_PATTERN, 
    message = ValidationConstants.PASSWORD_DESCRIPTION
)
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}