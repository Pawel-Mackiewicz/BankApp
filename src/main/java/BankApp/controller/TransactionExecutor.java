package BankApp.controller;

import BankApp.model.Transaction;
import BankApp.utils.LoggingService;
import BankApp.utils.Util;

public class TransactionExecutor implements Runnable {

    private final Transaction currentTransaction;

    public TransactionExecutor(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    @Override
    public void run() {
        lockAndLogAccounts();
        try {
            attemptTransaction();
        } finally {
            unlockAndLogAccounts();
        }
    }

    private void lockAndLogAccounts() {
        AccountLockManager.lockAccounts(currentTransaction.getTo(), currentTransaction.getFrom());
        LoggingService.logLockingAccounts(currentTransaction);
    }

    private void attemptTransaction() {
        LoggingService.logTransactionAttempt(currentTransaction);
        if (currentTransaction.isTransactionPossible()) {
            executeTransaction();
        } else {
            LoggingService.logFailedTransactionDueToInsufficientFunds(currentTransaction);
        }
    }

    private void executeTransaction() {
        Util.sleep(500);
        if (currentTransaction.execute()) {
            LoggingService.logSuccessfulTransaction(currentTransaction);
        } else {
            LoggingService.logErrorInMakingTransaction(currentTransaction);
        }
    }

    private void unlockAndLogAccounts() {
        AccountLockManager.unlockAccounts(currentTransaction.getFrom(), currentTransaction.getTo());
        LoggingService.logUnlockingAccounts(currentTransaction);
    }
}
