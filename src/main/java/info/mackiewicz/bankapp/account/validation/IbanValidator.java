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
     * Initializes the validator.
     *
     * <p>This implementation does nothing as no initialization is required.
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
     * <p>This method treats null values and strings that are empty or contain only whitespace as valid,
     * allowing additional annotations like {@code @NotNull} or {@code @NotBlank} to enforce non-nullity
     * or non-emptiness. For non-empty strings, it returns the result of {@code IbanUtil.isValid(value)}.</p>
     *
     * @param value the IBAN string to validate
     * @return {@code true} if {@code value} is null, empty, or valid per {@code IbanUtil}; {@code false} otherwise
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