package info.mackiewicz.bankapp.transaction.service.assembler;

import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
class TransactionTypeResolver {

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
