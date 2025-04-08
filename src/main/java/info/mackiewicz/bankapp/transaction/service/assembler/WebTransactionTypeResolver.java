package info.mackiewicz.bankapp.transaction.service.assembler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebTransactionTypeResolver {

    /**
     * Determines the transaction type for a web transfer request.
     *
     * <p>The method evaluates the request and selects the transaction type based on the following criteria:
     * <ul>
     *   <li>If the recipient IBAN is absent, the transaction is classified as an internal transfer (TRANSFER_INTERNAL).</li>
     *   <li>If the recipient IBAN is present and belongs to the same owner as the source IBAN,
     *   the transaction is classified as an own-account transfer (TRANSFER_OWN).</li>
     *   <li>Otherwise, the transaction type provided within the request is used.</li>
     * </ul>
     *
     * @param request the web transfer request carrying transaction details
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
     * Determines if a transfer should be processed as an internal transaction.
     *
     * <p>This method returns {@code true} when the recipient IBAN in the request is {@code null},
     * indicating the transfer is conducted via email and should be treated as an internal transfer.
     * It assumes that the request has been pre-validated to include at least one valid recipient identifier.
     *
     * @param request the web transfer request containing transfer details
     * @return {@code true} if the recipient IBAN is {@code null}; {@code false} otherwise
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
     * Determines whether the source and recipient IBANs in the provided web transfer request indicate ownership by the same account holder.
     * <p>
     * The check is performed by comparing the substring from the 6th to the 25th character of each IBAN.
     * </p>
     *
     * @param <T> the type that extends WebTransferRequest
     * @param request the web transfer request containing the IBANs to compare
     * @return true if the designated IBAN substrings match, indicating the same owner; false otherwise
     */
    private <T extends WebTransferRequest> boolean isSameOwner(T request) {
        log.debug("Checking if the source and recipient IBANs have the same owner");
        boolean isOwnTransfer = request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20);
        log.debug("Checking ownership: source IBAN {} and recipient IBAN {} - has same owner: {}",
                request.getSourceIban(), request.getRecipientIban(), isOwnTransfer);
        return isOwnTransfer;
    }
}
