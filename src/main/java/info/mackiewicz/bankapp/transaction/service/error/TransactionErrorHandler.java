package info.mackiewicz.bankapp.transaction.service.error;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.TransactionStatusManager;
import lombok.RequiredArgsConstructor;

/**
 * Centralizes error handling logic for transactions
 */
@RequiredArgsConstructor
@Component
public class TransactionErrorHandler {
    private final TransactionStatusManager statusManager;
    private final TransactionErrorNotifier errorNotifier;

    /**
     * Handles insufficient funds errors
     *
     * @param transaction The transaction that encountered the error
     * @param e The exception that was thrown
     */
    public void handleInsufficientFundsError(Transaction transaction, InsufficientFundsException e) {
        LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
        statusManager.setTransactionStatus(transaction, TransactionStatus.INSUFFICIENT_FUNDS);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles transaction validation errors
     *
     * @param transaction The transaction that encountered the error
     * @param e The exception that was thrown
     */
    public void handleValidationError(Transaction transaction, TransactionValidationException e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Validation Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.VALIDATION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles transaction execution errors
     *
     * @param transaction The transaction that encountered the error
     * @param e The exception that was thrown
     */
    public void handleExecutionError(Transaction transaction, TransactionExecutionException e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Execution Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles unexpected errors during transaction processing
     *
     * @param transaction The transaction that encountered the error
     * @param e The exception that was thrown
     */
    public void handleUnexpectedError(Transaction transaction, Exception e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Unexpected Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        errorNotifier.notifyError(transaction, new RuntimeException("Unexpected error during transaction processing", e));
    }
    
    public void handleLockError(Transaction transaction, AccountLockException e) {
        LoggingService.logErrorInLockingAccounts(e.getAccountId(), transaction, "Aquiring Lock Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }
    
    public void handleUnexpectedLockError(Transaction transaction, Exception e) {
        LoggingService.logUnexpectedErrorInLockingAccounts(transaction, "Unexpected Lock Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    public void handleUnlockError(Transaction transaction, AccountUnlockException e) {
        LoggingService.logErrorInUnlockingAccounts(e.getAccountId(), transaction, "Unlock Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    public void handleUnexpectedUnlockError(Transaction transaction, Exception e) {
        LoggingService.logUnexpectedErrorInUnlockingAccounts(transaction, "Unexpected Lock Error: " + e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }
    
    /**
     * Updates the status of a transaction in a thread-safe manner
     * 
     * @param transaction The transaction to update
     * @param status The new status to set
     */
    public void setTransactionStatus(Transaction transaction, TransactionStatus status) {
        statusManager.setTransactionStatus(transaction, status);
    }


}