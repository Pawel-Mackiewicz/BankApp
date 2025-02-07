package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.TransactionStatus;
import info.mackiewicz.bankapp.utils.AccountLockManager;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.utils.LoggingService;
import info.mackiewicz.bankapp.utils.Util;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class TransactionProcessingService {

    private final TransactionHydrator hydrator;

    public TransactionProcessingService(TransactionHydrator hydrator) {
        this.hydrator = hydrator;
    }

    @Async
    public void processTransaction(Transaction transaction) {
        lockAndLogAccounts(transaction);
        try {
            attemptTransaction(transaction);
        } finally {
            unlockAndLogAccounts(transaction);
        }
    }

    private void lockAndLogAccounts(Transaction transaction) {
        AccountLockManager.lockAccounts(transaction.getToAccount(), transaction.getFromAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    private void attemptTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);
        if (transaction.isTransactionPossible()) {
            executeTransaction(hydrateTransaction(transaction));
        } else {
            LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
        }
    }

    private Transaction hydrateTransaction(Transaction transaction) {
        return hydrator.hydrate(transaction);
    }

    private void executeTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.IN_PROGRESS);
        Util.sleep(200);
        if (transaction.execute()) {
            LoggingService.logSuccessfulTransaction(transaction);
            transaction.setStatus(TransactionStatus.DONE);
        } else {
            LoggingService.logErrorInMakingTransaction(transaction);
            transaction.setStatus(TransactionStatus.FAULTY);
        }
    }

    private void unlockAndLogAccounts(Transaction transaction) {
        AccountLockManager.unlockAccounts(transaction.getFromAccount(), transaction.getToAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }
}
