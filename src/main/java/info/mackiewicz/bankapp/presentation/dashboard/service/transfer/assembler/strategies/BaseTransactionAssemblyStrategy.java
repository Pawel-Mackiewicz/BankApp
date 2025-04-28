package info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler.strategies;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseTransactionAssemblyStrategy {

    protected <T extends WebTransferRequest> Transaction assembleTransaction(
            T request,
            Account sourceAccount,
            Account destinationAccount) {

        return buildTransfer(request, sourceAccount, destinationAccount);
    }

    protected <T extends WebTransferRequest> Transaction buildTransfer(T request, Account sourceAccount,
                                                                       Account destinationAccount) {
        log.debug("Building transaction with amount: {}", request.getAmount());

        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal(request.getAmount()))
                .withTitle(request.getTitle())
                .build();
        log.info("Transfer transaction assembled successfully with ID: {}", transaction.getId());

        return transaction;
    }

    protected abstract <T extends WebTransferRequest> void logTransferRequest(T request);

    protected abstract <T extends WebTransferRequest> Account getSourceAccount(T request);

    protected abstract <T extends WebTransferRequest> Account getDestinationAccount(T request);

}
