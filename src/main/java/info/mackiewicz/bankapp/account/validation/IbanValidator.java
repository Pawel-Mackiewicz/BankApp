package info.mackiewicz.bankapp.account.validation;

import org.iban4j.Iban;
import org.iban4j.IbanUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link Iban} annotation.
 * This class validates if an input represents a valid IBAN (International Bank
 * Account Number)
 * using the Iban4j library's validation utilities.
 *
 * This validator can handle both String and org.iban4j.Iban objects:
 * - For Iban objects, it automatically returns true (objects are already
 * validated)
 * - For String objects, it validates the format using IbanUtil
 */
public class IbanValidator implements ConstraintValidator<info.mackiewicz.bankapp.account.validation.Iban, Object> {

    /**
     * Initializes the validator with the annotation settings.
     * 
     * @param constraintAnnotation the annotation instance
     */
    @Override
    public void initialize(info.mackiewicz.bankapp.account.validation.Iban constraintAnnotation) {
    }

    /**
     * Validates whether the provided object represents a valid IBAN.
     * - If input is an org.iban4j.Iban object, returns true (already validated)
     * - If input is a String, validates using IbanUtil
     * - Returns true if the string is null or empty (validation will be handled
     * by @NotNull if required)
     * 
     * @param value   the value to validate, can be String or org.iban4j.Iban
     * @param context the validation context
     * @return true if the IBAN is valid or if the input is null/empty/Iban object,
     *         false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        // Null values are considered valid (can be handled by @NotNull if needed)
        if (value == null) return true;
        // If it's already an Iban object, it's valid by definition
        if (value instanceof Iban) return true;

        // If it's a String, perform standard validation
        if (value instanceof String) {
            String iban = (String) value;
            if (iban.trim().isEmpty()) {
                return true;
            }
            return IbanUtil.isValid(iban);
        }

        // For any other type, it's invalid
        return false;
    }
}