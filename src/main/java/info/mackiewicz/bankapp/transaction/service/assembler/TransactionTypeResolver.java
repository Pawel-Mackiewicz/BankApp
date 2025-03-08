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
     * If the recipient IBAN is null, then recipient email is not null, so it's an TRANSFER_INTERNAL.
     * Otherwise, it's an TRANSFER_OWN if the recipient IBAN is owned by the same user as the source IBAN.
     *
     * @param request the transfer request
     * @return resolved transaction type
     */
    public TransactionType resolveTransactionType(TransferRequest request) {
        log.debug("Resolving transaction type for request with source IBAN: {}", request.getSourceIban());

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
