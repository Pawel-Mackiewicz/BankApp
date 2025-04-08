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
     * No operation for initializing the validator.
     * <p>
     * This method fulfills the {@code ConstraintValidator} interface contract but requires no setup,
     * as this validator does not maintain any state.
     * </p>
     *
     * @param constraintAnnotation the DifferentAccounts annotation instance (unused)
     */
    @Override
    public void initialize(DifferentAccounts constraintAnnotation) {
        // No initialization needed
    }

    /**
     * Validates that the source and destination IBANs in a {@link WebTransferRequest} are different.
     * <p>
     * Returns {@code true} if the IBANs differ or if either is null (allowing other validations,
     * such as {@code @NotNull}, to handle null values). If the input is {@code null}, validation is bypassed.
     * For any other type, an {@link UnsupportedValidationTypeException} is thrown.
     * </p>
     *
     * @param value the transfer request to validate (expected to be a {@link WebTransferRequest})
     * @param context the validation context
     * @return {@code true} if the IBANs are different or if validation is bypassed; {@code false} if the IBANs are identical
     * @throws UnsupportedValidationTypeException if the provided value is not a {@link WebTransferRequest}
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
     * Validates that a transfer does not occur between the same account.
     *
     * <p>This method checks if both provided IBANs are non-null and distinct.
     * If either IBAN is null, it returns {@code true} to allow other validators to handle null checks.</p>
     *
     * @param sourceIban the IBAN of the account initiating the transfer
     * @param recipientIban the IBAN of the account receiving the transfer
     * @return {@code true} if the IBANs are different or if either is null, {@code false} otherwise
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