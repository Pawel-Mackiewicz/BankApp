package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.TransactionStatus;
import info.mackiewicz.bankapp.repository.TransactionRepository;
import info.mackiewicz.bankapp.utils.AccountLockManager;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.utils.LoggingService;
import info.mackiewicz.bankapp.utils.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionProcessor {

    private final TransactionHydrator hydrator;
    private final TransactionRepository repository;

    public TransactionProcessor(TransactionHydrator hydrator, TransactionRepository repository) {
        this.hydrator = hydrator;
        this.repository = repository;
    }

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
            changeTransactionStatus(transaction, TransactionStatus.IN_PROGRESS);
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
        AccountLockManager.unlockAccounts(transaction.getFromAccount(), transaction.getToAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }

    public void changeTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
