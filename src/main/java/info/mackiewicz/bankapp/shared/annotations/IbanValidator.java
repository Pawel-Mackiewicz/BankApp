package info.mackiewicz.bankapp.shared.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.iban4j.IbanUtil;

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

    @Override
    public void initialize(ValidIban constraintAnnotation) {
        // No initialization needed for this validator
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
        // Null values are considered valid (can be handled by @NotNull if needed)
        if (value == null) return true; 

        // Empty strings are considered valid (can be handled by @NotBlank if needed)
        if (value.trim().isEmpty()) return true; 

        return IbanUtil.isValid(value);
    }
}