package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.controller.AccountLockManager;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.utils.LoggingService;
import info.mackiewicz.bankapp.utils.Util;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class TransactionProcessingService {

    @Async
    @Transactional
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
            executeTransaction(transaction);
        } else {
            LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
        }
    }

    private void executeTransaction(Transaction transaction) {
        Util.sleep(500);
        if (transaction.execute()) {
            LoggingService.logSuccessfulTransaction(transaction);
        } else {
            LoggingService.logErrorInMakingTransaction(transaction);
        }
    }

    private void unlockAndLogAccounts(Transaction transaction) {
        AccountLockManager.unlockAccounts(transaction.getFromAccount(), transaction.getToAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }
}
