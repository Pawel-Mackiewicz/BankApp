package info.mackiewicz.bankapp.shared.annotations;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * Annotation for validating email fields.
 * <p>
 * This annotation is used to enforce specific email address validation rules, including:<br>
 * - The field must not be blank.<br>
 * - The email must match the specified pattern defined in {@link ValidationConstants#EMAIL_PATTERN}.
 * <p>
 * Can be applied to fields or parameters for validation purposes during runtime.
 * <p>
 * Validation errors will display the default message or a custom one provided in the annotation.
 * <p>
 * For example, it ensures emails like "example@domain.com" are valid.
 */
@NotBlank(message = "Email is required")
@Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
@ReportAsSingleViolation
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Invalid email format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
