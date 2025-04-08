package info.mackiewicz.bankapp.transaction.service.assembler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebTransactionTypeResolver {

    /**
     * Determines the transaction type for a given web transfer request.
     *
     * <p>The transaction type is resolved according to the following rules:
     * <ul>
     *   <li>If the recipient IBAN is absent, the transfer is classified as an internal transfer.</li>
     *   <li>If the source and recipient IBANs belong to the same user, the transfer is considered an own transfer.</li>
     *   <li>Otherwise, the transaction type specified in the request is used.</li>
     * </ul>
     *
     * @param request the web transfer request containing transaction details
     * @return the resolved transaction type
     */
    public <T extends WebTransferRequest> TransactionType resolveTransactionType(T request) {
        log.debug("Resolving transaction type for request with source IBAN: {}", request.getSourceIban());

        TransactionType resolvedType =
                isThroughEmail(request) ? TransactionType.TRANSFER_INTERNAL
                : isSameOwner(request) ? TransactionType.TRANSFER_OWN : request.getTransactionType();

        log.debug("Transaction type resolved as: {}", resolvedType);

        return resolvedType;
    }

    /**
     * Determines whether the transfer request represents an internal transfer initiated via email.
     * <p>
     * This method returns true if the recipient IBAN is absent, implying that the transfer is handled internally.
     * It assumes that the request has been pre-validated to ensure that a recipient email is provided when the IBAN is missing.
     * </p>
     *
     * @param <T> the type of the web transfer request
     * @param request the transfer request containing the necessary transfer details
     * @return true if the transfer is identified as an internal email transfer, false otherwise
     */
    private <T extends WebTransferRequest> boolean isThroughEmail(T request) {
        log.debug("Checking if the transfer is through email");
        if (request.getRecipientIban() == null) {
            log.debug("Recipient IBAN is null, resolving as TRANSFER_INTERNAL");
            return true;
        }
        return false;
    }

    /**
     * Determines if the source and recipient IBANs in the transfer request belong to the same account owner.
     * <p>
     * The method compares a specific substring of both IBANs—starting at the 6th character and spanning 20 characters—
     * that represents the owner's unique identifier.
     * </p>
     *
     * @param <T> a WebTransferRequest containing the source and recipient IBANs
     * @param request the transfer request holding the IBAN details
     * @return {@code true} if the designated IBAN segments match, indicating the same owner; {@code false} otherwise
     */
    private <T extends WebTransferRequest> boolean isSameOwner(T request) {
        log.debug("Checking if the source and recipient IBANs have the same owner");
        boolean isOwnTransfer = request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20);
        log.debug("Checking ownership: source IBAN {} and recipient IBAN {} - has same owner: {}",
                request.getSourceIban(), request.getRecipientIban(), isOwnTransfer);
        return isOwnTransfer;
    }
}
