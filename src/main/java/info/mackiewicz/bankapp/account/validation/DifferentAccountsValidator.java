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
     * Validates that the source and destination accounts in a transfer request are different.
     * <p>
     * This method processes instances of {@code WebTransferRequest} by comparing the source and recipient
     * IBANs, returning {@code true} if they are different. If the provided value is {@code null}, the
     * validation is bypassed to allow other constraints (such as {@code @NotNull}) to handle null checks.
     * For any unsupported type, an {@link UnsupportedValidationTypeException} is thrown.
     *
     * @param value   the transfer request to validate; expected to be a {@code WebTransferRequest}
     * @param context the validation context
     * @return {@code true} if the IBANs are different or if validation is not applicable (e.g., when the value is {@code null})
     * @throws UnsupportedValidationTypeException if the provided value is not a {@code WebTransferRequest}
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

    /**
     * Validates that the source and recipient IBANs are distinct.
     * <p>
     * If either IBAN is {@code null}, the method returns {@code true} to allow for external null validations.
     * Otherwise, it returns {@code true} if the IBANs are different and {@code false} if they are identical.
     * </p>
     *
     * @param sourceIban    the IBAN of the source account
     * @param recipientIban the IBAN of the recipient account
     * @return {@code true} if the IBANs are different or if either is {@code null}; {@code false} if both are identical
     */
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