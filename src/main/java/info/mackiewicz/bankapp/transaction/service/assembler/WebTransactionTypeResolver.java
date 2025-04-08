package info.mackiewicz.bankapp.transaction.service.assembler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebTransactionTypeResolver {

    /**
     * Resolves the transaction type for a given web transfer request.
     *
     * <p>This method determines the transaction type based on the following rules:
     * <ul>
     *   <li>If the recipient IBAN is not provided, the transaction is considered internal and
     *       categorized as TRANSFER_INTERNAL.
     *   <li>If the recipient IBAN is provided and indicates the same owner as the source IBAN,
     *       the transaction is categorized as TRANSFER_OWN.
     *   <li>Otherwise, the transaction type specified in the request is used.
     * </ul>
     *
     * @param request the web transfer request containing the transfer details
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
     * Determines whether the transfer is initiated via email.
     *
     * <p>This method returns {@code true} if the recipient IBAN in the provided transfer request is {@code null},
     * indicating that the transfer should be processed as an internal (email-based) transaction. It assumes that
     * request validation has ensured the presence of either a recipient IBAN or recipient email.
     *
     * @param <T> a type that extends {@code WebTransferRequest}
     * @param request the transfer request containing transaction details
     * @return {@code true} if the recipient IBAN is {@code null}, {@code false} otherwise
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
     * Determines if the source and recipient IBANs belong to the same account owner.
     * <p>
     * The check compares the substring from the 6th to the 25th character of both IBANs.
     * A match indicates that the IBANs are owned by the same account holder.
     * </p>
     *
     * @param <T> the type of web transfer request that extends WebTransferRequest
     * @param request the web transfer request containing the source and recipient IBANs
     * @return true if the IBANs indicate the same account owner; false otherwise
     */
    private <T extends WebTransferRequest> boolean isSameOwner(T request) {
        log.debug("Checking if the source and recipient IBANs have the same owner");
        boolean isOwnTransfer = request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20);
        log.debug("Checking ownership: source IBAN {} and recipient IBAN {} - has same owner: {}",
                request.getSourceIban(), request.getRecipientIban(), isOwnTransfer);
        return isOwnTransfer;
    }
}
