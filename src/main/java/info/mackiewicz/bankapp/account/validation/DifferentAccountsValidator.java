package info.mackiewicz.bankapp.account.validation;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Validator implementation for the {@link DifferentAccounts} annotation.
 * Ensures that source and destination accounts in transfer requests are different.
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
     * Validates that the source and destination accounts in a transfer request are different.
     * Handles both internal and external transfer request types.
     * 
     * @param value the object to validate (expected to be a transfer request)
     * @param context the validation context
     * @return true if accounts are different or if validation is not applicable, false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        log.debug("Validating account difference for: {}", value);
        
        if (value == null) {
            log.debug("Validation skipped - value is null");
            return true; // Let @NotNull handle null validation
        }

        if (value instanceof InternalTransferRequest) {
            return validateInternalTransfer((InternalTransferRequest) value);
        } else if (value instanceof ExternalTransferRequest) {
            return validateExternalTransfer((TransferRequest) value);
        }

        log.debug("Validation skipped - not a transfer request");
        return true; // Not a transfer request, validation not applicable
    }

    /**
     * Validates that source and recipient IBANs are different for internal transfers.
     * 
     * @param request the internal transfer request to validate
     * @return true if IBANs are different or if any IBAN is null, false if they are the same
     */
    private boolean validateInternalTransfer(InternalTransferRequest request) {
        log.debug("Validating internal transfer: sourceIban={}, recipientIban={}", 
                 request.getSourceIban(), request.getRecipientIban());
        
        if (request.getSourceIban() == null || request.getRecipientIban() == null) {
            log.debug("Validation skipped - IBAN is null");
            return true; // Let other validators handle null values
        }
        
        boolean result = !request.getSourceIban().equals(request.getRecipientIban());
        log.debug("Internal transfer validation result: {}", result);
        return result;
    }

    /**
     * Validates that source and recipient IBANs are different for external transfers.
     * 
     * @param request the external transfer request to validate
     * @return true if IBANs are different or if any IBAN is null, false if they are the same
     */
    private boolean validateExternalTransfer(TransferRequest request) {
        log.debug("Validating external transfer: sourceIban={}, recipientIban={}", 
                 request.getSourceIban(), request.getRecipientIban());
        
        if (request.getSourceIban() == null || request.getRecipientIban() == null) {
            log.debug("Validation skipped - IBAN is null");
            return true; // Let other validators handle null values
        }

        boolean result = !request.getSourceIban().equals(request.getRecipientIban());
        log.debug("External transfer validation result: {}", result);
        return result;
    }
}