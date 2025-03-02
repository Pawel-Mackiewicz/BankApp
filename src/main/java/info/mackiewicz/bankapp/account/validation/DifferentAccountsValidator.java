package info.mackiewicz.bankapp.account.validation;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Validator for @DifferentAccounts annotation.
 * Ensures that source and destination accounts in a transfer request are different.
 */
@Slf4j
@Component
public class DifferentAccountsValidator implements ConstraintValidator<DifferentAccounts, Object> {

    @Override
    public void initialize(DifferentAccounts constraintAnnotation) {
        // No initialization needed
    }

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