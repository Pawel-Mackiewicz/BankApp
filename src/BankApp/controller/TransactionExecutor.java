package BankApp.controller;

import BankApp.model.Transaction;
import BankApp.utils.LoggingService;
import BankApp.utils.Util;

public class TransactionExecutor implements Runnable {

    private Transaction currentTransaction;

    public TransactionExecutor(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    @Override
    public void run() {
        AccountLockManager.lockAccounts(currentTransaction.getTo(), currentTransaction.getFrom());
        LoggingService.logLockingAccounts(currentTransaction);
        try {
            LoggingService.logTransactionAttempt(currentTransaction);
            if (currentTransaction.isTransactionPossible()) {
                makeTransaction();
                LoggingService.logSuccessfulTransaction(currentTransaction);
            } else {
                LoggingService.logFailedTransactionDueToInsufficientFunds(currentTransaction);
            }
        } finally {
            AccountLockManager.unlockAccounts(currentTransaction.getFrom(), currentTransaction.getTo());
            LoggingService.logUnlockingAccounts(currentTransaction);
        }
    }

    private void makeTransaction() {
        Util.sleep(500);
        currentTransaction.execute();
    }
}
