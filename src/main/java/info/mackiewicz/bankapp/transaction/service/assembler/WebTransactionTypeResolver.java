package info.mackiewicz.bankapp.transaction.service.assembler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebTransactionTypeResolver {

    /**
     * Resolves the transaction type based on the request data.
     * If the recipient IBAN is null, then recipient email should not be null, so
     * it's an TRANSFER_INTERNAL.
     * Otherwise, it's an TRANSFER_OWN if the recipient IBAN is owned by the same
     * user as the source IBAN.
     * Otherwise, it's the transaction type from the request.
     *
     * @param request the transfer request
     * @return resolved transaction type
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
     * Checks if the transfer is through email.
     * It's a job of the validator to ensure that at least one of the fields is not
     * null and that the recipient email is not null if the recipient IBAN is null.
     * If the recipient IBAN is null, then recipient email should not be null,
     * so it's an TRANSFER_INTERNAL, because only internal transfers can have a null IBAN.
     * 
     * @param <T>
     * @param request
     * @return true if the transfer is through email, false otherwise
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
     * Checks if the source and recipient IBANs have the same owner.
     * Checking is done by comparing the 6th to 25th characters of the IBANs.
     * If they are the same, then the IBANs have the same owner.
     * 
     * @param <T>     type of TransferRequest
     * @param request the transfer request
     * @return true if the source and recipient IBANs have the same owner, false
     *         otherwise
     */
    private <T extends WebTransferRequest> boolean isSameOwner(T request) {
        log.debug("Checking if the source and recipient IBANs have the same owner");
        boolean isOwnTransfer = request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20);
        log.debug("Checking ownership: source IBAN {} and recipient IBAN {} - has same owner: {}",
                request.getSourceIban(), request.getRecipientIban(), isOwnTransfer);
        return isOwnTransfer;
    }
}
