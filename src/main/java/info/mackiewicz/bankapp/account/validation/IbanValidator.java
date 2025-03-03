package info.mackiewicz.bankapp.account.validation;

import org.iban4j.IbanUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link Iban} annotation.
 * This class validates if a string represents a valid IBAN (International Bank Account Number)
 * using the Iban4j library's validation utilities.
 */
public class IbanValidator implements ConstraintValidator<Iban, String> {

    /**
     * Initializes the validator with the annotation settings.
     * 
     * @param constraintAnnotation the annotation instance
     */
    @Override
    public void initialize(Iban constraintAnnotation) {
    }

    /**
     * Validates whether the provided string is a valid IBAN.
     * Returns true if the string is null or empty (validation will be handled by @NotNull if required).
     * 
     * @param iban the IBAN string to validate
     * @param context the validation context
     * @return true if the IBAN is valid or if the input is null/empty, false otherwise
     */
    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null || iban.trim().isEmpty()) {
            return true;
        }
        return IbanUtil.isValid(iban);
    }
}