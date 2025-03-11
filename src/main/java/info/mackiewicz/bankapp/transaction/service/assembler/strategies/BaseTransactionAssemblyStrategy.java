package info.mackiewicz.bankapp.transaction.service.assembler.strategies;

import java.math.BigDecimal;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseTransactionAssemblyStrategy {

    protected <T extends TransferRequest> Transaction assembleTransaction(
            T request,
            Account sourceAccount,
            Account destinationAccount,
            TransactionType resolvedType) {

        return buildTransfer(request, sourceAccount, destinationAccount, resolvedType);
    }

    protected <T extends TransferRequest> Transaction buildTransfer(T request, Account sourceAccount,
            Account destinationAccount, TransactionType resolvedType) {
        log.debug("Building transaction with amount: {}", request.getAmount());

        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTitle(request.getTitle())
                .withTransactionType(resolvedType)
                .build();
        log.info("Transfer transaction assembled successfully with ID: {}", transaction.getId());

        return transaction;
    }

    protected abstract <T extends TransferRequest> void logTransferRequest(T request);

    protected abstract <T extends TransferRequest> Account getSourceAccount(T request);

    protected abstract <T extends TransferRequest> Account getDestinationAccount(T request);

}
