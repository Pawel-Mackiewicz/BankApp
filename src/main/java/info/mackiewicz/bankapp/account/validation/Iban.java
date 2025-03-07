package info.mackiewicz.bankapp.account.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation used to validate that a field represents a valid IBAN (International Bank Account Number).
 * This annotation can be applied to fields or methods to enforce IBAN format validation.
 * The validation itself is performed by the {@link IbanValidator} class.
 */
@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Iban {
    /**
     * Error message to be displayed when validation fails.
     * 
     * @return error message string
     */
    String message() default "Invalid IBAN";
    
    /**
     * Groups that this constraint belongs to.
     * 
     * @return validation groups
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload that can be attached to a constraint declaration.
     * 
     * @return payload data
     */
    Class<? extends Payload>[] payload() default {};
}