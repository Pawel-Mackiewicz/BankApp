package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.TransactionStatus;
import info.mackiewicz.bankapp.repository.TransactionRepository;
import info.mackiewicz.bankapp.utils.AccountLockManager;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.utils.LoggingService;
import info.mackiewicz.bankapp.utils.Util;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TransactionProcessor {

    private final TransactionHydrator hydrator;
    private final TransactionRepository repository;


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
        AccountLockManager.lockAccounts(transaction.getDestinationAccount(), transaction.getSourceAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    private void attemptTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);

        if (transaction.isTransactionPossible()) {
            changeTransactionStatus(transaction, TransactionStatus.PENDING);
            executeTransaction(hydrateTransaction(transaction));
        } else {
            LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
            changeTransactionStatus(transaction, TransactionStatus.FAULTY);
        }
    }

    private Transaction hydrateTransaction(Transaction transaction) {

        return hydrator.hydrate(transaction);
    }

    private void executeTransaction(Transaction transaction) {
        Util.sleep(200);

        boolean success = transaction.execute();
        if (success) {
            LoggingService.logSuccessfulTransaction(transaction);
            changeTransactionStatus(transaction, TransactionStatus.DONE);
        } else {
            LoggingService.logErrorInMakingTransaction(transaction);
            changeTransactionStatus(transaction, TransactionStatus.FAULTY);
        }
    }

    private void unlockAndLogAccounts(Transaction transaction) {
        AccountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }

    public void changeTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
