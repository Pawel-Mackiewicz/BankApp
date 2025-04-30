package info.mackiewicz.bankapp.shared.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that source and destination accounts in a transfer request are different.
 * Can be applied to transfer request DTOs that contain source and destination account information.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifferentAccountsValidator.class)
@Documented
public @interface DifferentAccounts {
    String message() default "Source and destination accounts must be different";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}