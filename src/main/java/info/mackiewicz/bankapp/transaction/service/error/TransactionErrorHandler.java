package info.mackiewicz.bankapp.transaction.service.error;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.TransactionStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralizes error handling logic for transactions
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionErrorHandler {
    private final TransactionStatusManager statusManager;
    private final TransactionErrorNotifier errorNotifier;

    /**
     * Handles insufficient funds errors.
     * This is a business validation error, logged as WARN.
     */
    public void handleInsufficientFundsError(Transaction transaction, InsufficientFundsException e) {
        log.warn("Transaction {} failed: Insufficient funds - {}", transaction.getId(), e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.INSUFFICIENT_FUNDS);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles transaction validation errors.
     * These are business validation errors, logged as WARN.
     */
    public void handleValidationError(Transaction transaction, Exception e) {
        log.warn("Transaction {} validation failed: {}", transaction.getId(), e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.VALIDATION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles unexpected errors during transaction processing.
     * These are system errors, logged as ERROR.
     */
    public void handleUnexpectedError(Transaction transaction, Exception e) {
        log.error("Transaction {} failed with unexpected error: {}", transaction.getId(), e.getMessage(), e);
        statusManager.setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles account lock acquisition errors.
     * These are concurrency-related errors, logged as WARN.
     */
    public void handleLockError(Transaction transaction, AccountLockException e) {
        log.warn("Failed to acquire lock for account {} in transaction {}: {}",
                e.getAccountId(), transaction.getId(), e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles unexpected lock-related errors.
     * These are system errors, logged as ERROR.
     */
    public void handleUnexpectedLockError(Transaction transaction, Exception e) {
        log.error("Unexpected error while acquiring locks for transaction {}: {}",
                transaction.getId(), e.getMessage(), e);
        statusManager.setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles account unlock errors.
     * These are concurrency-related errors, logged as WARN.
     */
    public void handleUnlockError(Transaction transaction, AccountUnlockException e) {
        log.warn("Failed to release lock for account {} in transaction {}: {}",
                e.getAccountId(), transaction.getId(), e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles unexpected unlock-related errors.
     * These are system errors, logged as ERROR.
     */
    public void handleUnexpectedUnlockError(Transaction transaction, Exception e) {
        log.error("Unexpected error while releasing locks for transaction {}: {}",
                transaction.getId(), e.getMessage(), e);
        statusManager.setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    /**
     * Handles transaction status change errors.
     * These are execution errors, logged as WARN.
     */
    public void handleTransactionStatusChangeError(Transaction transaction, Exception e) {
        log.warn("Failed to update status for transaction {}: {}", transaction.getId(), e.getMessage());
        statusManager.setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

}