package info.mackiewicz.bankapp.account.validation;

import org.iban4j.IbanUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for IBAN (International Bank Account Number) validation.
 * Implements {@link ConstraintValidator} to provide validation logic for the {@link ValidIban} annotation.
 * This validator checks if a given string represents a valid IBAN number using the IbanUtil library.
 * 
 * <p>The validator considers null values and empty strings as valid. If these need to be invalid,
 * additional constraints like {@code @NotNull} or {@code @NotBlank} should be used.</p>
 *
 * @see ValidIban
 * @see ConstraintValidator
 * @see IbanUtil
 */
public class IbanValidator implements ConstraintValidator<ValidIban, String> {

    /**
     * Initializes the IBAN validator.
     *
     * <p>No initialization is required for this validator.</p>
     *
     * @param constraintAnnotation the ValidIban annotation instance (unused)
     */
    @Override
    public void initialize(ValidIban constraintAnnotation) {
        // No initialization needed for this validator
    }

    /**
     * Validates the provided IBAN string.
     *
     * <p>
     * This method treats {@code null} and blank strings (after trimming) as valid, allowing other constraints such as
     * {@code @NotNull} or {@code @NotBlank} to handle these cases. For non-blank strings, validation is delegated to
     * {@link IbanUtil#isValid(String)}.
     * </p>
     *
     * @param value the IBAN string to validate (may be {@code null} or blank)
     * @param context context in which the constraint is evaluated
     * @return {@code true} if the value is {@code null}, blank, or a valid IBAN; {@code false} otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
        // Null values are considered valid (can be handled by @NotNull if needed)
        if (value == null) return true; 

        // Empty strings are considered valid (can be handled by @NotBlank if needed)
        if (value.trim().isEmpty()) return true; 

        return IbanUtil.isValid(value);
    }
}