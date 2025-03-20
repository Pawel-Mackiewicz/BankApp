package info.mackiewicz.bankapp.shared.util;

import lombok.experimental.UtilityClass;
import org.iban4j.IbanUtil;

/**
 * Utility class providing common IBAN validation logic.
 * Used by various validators throughout the application.
 *
 * @see org.iban4j.IbanUtil
 * @see lombok.experimental.UtilityClass
 */
@UtilityClass
public class IbanValidationUtil {
    
    /**
     * Validates whether the provided IBAN number is correct.
     * 
     * @param iban The IBAN number to validate
     * @return true if the IBAN is valid, false otherwise
     * @throws IllegalArgumentException if the IBAN format is invalid
     * @see org.iban4j.IbanUtil#isValid(String)
     */
    public boolean isValid(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            return false;
        }
        try {
            return IbanUtil.isValid(iban);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates whether the provided IBAN number is correct in the context of Bean Validation.
     * Differs from regular validation by treating empty values as valid
     * (empty value validation should be handled by @NotNull/@NotEmpty annotations).
     * 
     * @param iban The IBAN number to validate
     * @return true if the IBAN is valid or empty, false if it's invalid
     * @throws IllegalArgumentException if the IBAN format is invalid
     * @see javax.validation.constraints.NotNull
     * @see javax.validation.constraints.NotEmpty
     * @see org.iban4j.IbanUtil#isValid(String)
     */
    public boolean isValidForBeanValidation(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            return true; // Empty values are accepted in Bean Validation context
        }
        try {
            return IbanUtil.isValid(iban);
        } catch (Exception e) {
            return false;
        }
    }
}