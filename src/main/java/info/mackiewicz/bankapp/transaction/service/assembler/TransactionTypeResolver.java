package info.mackiewicz.bankapp.transaction.service.assembler;

import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TransactionTypeResolver {

    /**
     * Resolves the transaction type based on the request data.
     * If the recipient IBAN is null, then recipient email should not be null, so it's an TRANSFER_INTERNAL.
     * Otherwise, it's an TRANSFER_OWN if the recipient IBAN is owned by the same user as the source IBAN.
     * Otherwise, it's the transaction type from the request.
     *
     * @param request the transfer request
     * @return resolved transaction type
     */
    public TransactionType resolveTransactionType(TransferRequest request) {
        log.debug("Resolving transaction type for request with source IBAN: {}", request.getSourceIban());
        // It's job of the validator to ensure that at least one of the fields is not null
        // and that the recipient email is not null if the recipient IBAN is null
        // so we can safely assume that the recipient email is not null if the recipient IBAN is null
        if (request.getRecipientIban() == null) {
            log.debug("Recipient IBAN is null, resolving as TRANSFER_INTERNAL");
            return TransactionType.TRANSFER_INTERNAL;
        }

        boolean isOwnTransfer = request.getSourceIban().regionMatches(5, request.getRecipientIban(), 5, 20);
        TransactionType resolvedType = isOwnTransfer ? TransactionType.TRANSFER_OWN : request.getTransactionType();
        log.debug("Transaction type resolved as: {}", resolvedType);
        return resolvedType;
    }
}
