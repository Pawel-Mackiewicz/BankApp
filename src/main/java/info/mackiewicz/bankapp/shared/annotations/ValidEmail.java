package info.mackiewicz.bankapp.shared.annotations;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@NotBlank(message = "Email is required")
@Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Invalid email format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
