package info.mackiewicz.bankapp.transaction.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.account.exception.AccountValidationException;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.error.TransactionFailureHandler;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for processing financial transactions with proper locking
 * and validation mechanisms.
 * Focuses solely on executing transactions with proper locking, without
 * duplicating validation logic.
 */
@RequiredArgsConstructor
@Service
public class TransactionProcessor {

    private final StrategyResolver strategyResolver;
    private final AccountLockManager accountLockManager;
    private final TransactionFailureHandler errorHandler;
    private final TransactionStatusManager statusManager;

    /**
     * Asynchronously processes a financial transaction with proper account locking
     * and error handling.
     * Assumes the transaction has already passed validation.
     * Errors are handled by the TransactionErrorHandler.
     * @param transaction transaction to process
     */
    @Async
    public void processTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);
        try {
            acquireAccountLocks(transaction);
            try {
                // Set status to PENDING before executing
                updateTransactionStatus(transaction, TransactionStatus.PENDING);

                // Execute the transaction strategy
                executeTransactionStrategy(transaction);

                // Update status to DONE after successful execution
                updateTransactionStatus(transaction, TransactionStatus.DONE);
                LoggingService.logSuccessfulTransaction(transaction);
            } catch (TransactionExecutionException e) {
                // This exception is handled inside the method
                return;
            } catch (Exception e) {
                errorHandler.handleUnexpectedError(transaction, e);
            }
        } catch (AccountLockException e) {
            errorHandler.handleLockError(transaction, e);
        } catch (Exception e) {
            errorHandler.handleUnexpectedLockError(transaction, e);
        } finally {
            try {
                releaseAccountLocks(transaction);
            } catch (AccountUnlockException e) {
                errorHandler.handleUnlockError(transaction, e);
            } catch (Exception e) {
                errorHandler.handleUnexpectedUnlockError(transaction, e);
            }
        }
    }

    private void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        try {
            statusManager.setTransactionStatus(transaction, status);
        } catch (TransactionNotFoundException e) {
            errorHandler.handleTransactionStatusChangeError(transaction, e);
            throw new TransactionExecutionException();
        } catch (IllegalArgumentException e) {
            errorHandler.handleTransactionStatusChangeError(transaction, e);
            throw new TransactionExecutionException();
        } catch (IllegalStateException e) {
            errorHandler.handleTransactionStatusChangeError(transaction, e);
            throw new TransactionExecutionException();
        }
    }

    private void acquireAccountLocks(Transaction transaction) {
        accountLockManager.lockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    private void executeTransactionStrategy(Transaction transaction) {
        TransactionStrategy strategy = strategyResolver.resolveStrategy(transaction);
        try {
            strategy.execute(transaction);
        } catch (AccountValidationException e) {
            errorHandler.handleValidationError(transaction, e);
            throw new TransactionExecutionException();
        } catch (InsufficientFundsException e) {
            errorHandler.handleInsufficientFundsError(transaction, e);
            throw new TransactionExecutionException();
        } catch (Exception e) {
            errorHandler.handleSystemError(transaction, e);
            throw new TransactionExecutionException();
        }
    }

    private void releaseAccountLocks(Transaction transaction) {
        accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }
}
