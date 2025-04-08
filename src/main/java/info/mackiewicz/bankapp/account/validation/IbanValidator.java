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
     * @param constraintAnnotation the ValidIban annotation instance used to configure the validator
     */
    @Override
    public void initialize(ValidIban constraintAnnotation) {
        // No initialization needed for this validator
    }

    /**
     * Validates the given IBAN string.
     *
     * <p>
     * This method considers {@code null} values and empty strings (after trimming) as valid, allowing such cases to be managed by additional constraints
     * (e.g., {@code @NotNull} or {@code @NotBlank}). For non-empty strings, validity is determined via {@code IbanUtil.isValid(value)}.
     * </p>
     *
     * @param value   the IBAN string to validate; may be {@code null} or empty
     * @param context the context in which the constraint is validated
     * @return {@code true} if the IBAN is valid, or if the input is {@code null} or empty; {@code false} otherwise
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