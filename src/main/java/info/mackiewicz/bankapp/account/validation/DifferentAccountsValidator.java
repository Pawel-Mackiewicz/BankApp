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
     * Validates that the source and destination accounts in a WebTransferRequest are different.
     * <p>
     * If the provided value is an instance of WebTransferRequest, the source and recipient IBANs are compared.
     * The validation returns true if the IBANs differ, or if either IBAN is null (to allow separate null handling).
     * </p>
     * <p>
     * If the input value is null, validation is bypassed and true is returned.
     * For any unsupported transfer request type, an UnsupportedValidationTypeException is thrown.
     * </p>
     *
     * @param value   the transfer request object to validate (expected to be a WebTransferRequest)
     * @param context the validation context
     * @return true if the accounts are considered different or validation is bypassed; false otherwise
     * @throws UnsupportedValidationTypeException if the transfer request type is not supported
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
     * Validates that a transfer is only performed between two different IBANs.
     * <p>
     * If either the source or recipient IBAN is null, the validation is skipped,
     * returning true to allow other validators to handle null values. Otherwise,
     * the method returns true if the IBANs are different, and false if they are identical.
     *
     * @param sourceIban    the IBAN of the source account
     * @param recipientIban the IBAN of the recipient account
     * @return true if the IBANs are different or if a null value is present, false otherwise
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