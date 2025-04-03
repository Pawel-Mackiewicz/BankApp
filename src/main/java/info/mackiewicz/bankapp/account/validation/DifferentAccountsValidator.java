package info.mackiewicz.bankapp.account.validation;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.exception.UnsupportedValidationTypeException;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Validator implementation for the {@link DifferentAccounts} annotation.
 * Ensures that source and destination accounts in transfer requests are
 * different.
 * This validator supports both internal and external transfer request types and
 * prevents users from attempting to transfer money to the same account.
 */
@Slf4j
@Component
public class DifferentAccountsValidator implements ConstraintValidator<DifferentAccounts, Object> {

    /**
     * Initializes the validator with the annotation settings.
     * 
     * @param constraintAnnotation the annotation instance
     */
    @Override
    public void initialize(DifferentAccounts constraintAnnotation) {
        // No initialization needed
    }

    /**
     * Validates that the source and destination accounts in a transfer request are
     * different.
     * Handles both internal and external transfer request types.
     * 
     * @param value   the object to validate (expected to be a transfer request)
     * @param context the validation context
     * @return true if accounts are different or if validation is not applicable,
     *         false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        log.debug("Validating account difference for: {}", value);
        switch (value) {
            case WebTransferRequest webTransferRequest -> {
                log.debug("Validating WebTransferRequest: {}", webTransferRequest);
                return validateTransfer(webTransferRequest.getSourceIban(),
                webTransferRequest.getRecipientIban());
            }
            case null -> {
                log.debug("Validation skipped - value is null");
                return true; // Let @NotNull handle null validation
            }
            default -> {
                throw new UnsupportedValidationTypeException(
                        "Invalid transfer request type: " + value.getClass().getName());
            }
        }
    }

    private boolean validateTransfer(String sourceIban, String recipientIban) {
        log.debug("Validating IBAN transfer: sourceIban={}, recipientIban={}",
                sourceIban, recipientIban);

        if (sourceIban == null || recipientIban == null) {
            log.debug("Validation skipped - IBAN is null");
            return true; // Let other validators handle null values
        }

        boolean result = !sourceIban.equals(recipientIban);
        log.debug("Internal transfer validation result: {}", result);
        return result;
    }

}