package info.mackiewicz.bankapp.transaction.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.account.exception.AccountValidationException;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutorRegistry;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutor;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for processing financial transactions with proper
 * locking and validation mechanisms.
 */
@RequiredArgsConstructor
@Service
class TransactionProcessor {

    private final AccountService accountService;
    private final AccountLockManager accountLockManager;
    private final TransactionErrorHandler errorHandler;
    private final TransactionStatusManager statusManager;
    private final LoggingService loggingService;
    private final TransactionExecutorRegistry commandRegistry;

    /**
     * Asynchronously processes a financial transaction with proper account locking
     * and error handling.
     * Assumes the transaction has already passed validation.
     * Errors are handled by the TransactionErrorHandler.
     * 
     * @param transaction transaction to process
     */
    @Async
    public void processTransaction(Transaction transaction) {
        loggingService.logTransactionAttempt(transaction);
        try {
            acquireAccountLocks(transaction);
            try {
                executeWithStatusUpdates(transaction);
            } catch (TransactionExecutionException e) {
                return;
            } catch (Exception e) {
                errorHandler.handleUnexpectedError(transaction, e);
            }
        } catch (AccountLockException e) {
            errorHandler.handleLockError(transaction, e);
        } catch (Exception e) {
            errorHandler.handleUnexpectedLockError(transaction, e);
        } finally {
            releaseAccountLocks(transaction);
        }
    }

    private void executeWithStatusUpdates(Transaction transaction) {
            updateTransactionStatus(transaction, TransactionStatus.PENDING);
            executeTransaction(transaction);
            updateTransactionStatus(transaction, TransactionStatus.DONE);
            loggingService.logSuccessfulTransaction(transaction);
    }

    private void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        try {
            statusManager.setTransactionStatus(transaction, status);
        } catch (TransactionNotFoundException | IllegalStateException | IllegalArgumentException e) {
            errorHandler.handleTransactionStatusChangeError(transaction, e);
            throw new TransactionExecutionException();
        }
    }

    private void executeTransaction(Transaction transaction) {
        try {
            // Get command based on transaction type
            TransactionExecutor command = commandRegistry.getCommand(transaction.getType());
            
            // Execute the transaction using the appropriate command
            command.execute(transaction, accountService);
        } catch (AccountValidationException e) {
            errorHandler.handleValidationError(transaction, e);
        } catch (InsufficientFundsException e) {
            errorHandler.handleInsufficientFundsError(transaction, e);
            throw e;
        } catch (Exception e) {
            errorHandler.handleUnexpectedError(transaction, e);
        }
    }

    private void acquireAccountLocks(Transaction transaction) {
        accountLockManager.lockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        loggingService.logLockingAccounts(transaction);
    }

    private void releaseAccountLocks(Transaction transaction) {
        try {
            accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
            loggingService.logUnlockingAccounts(transaction);
        } catch (AccountUnlockException e) {
            errorHandler.handleUnlockError(transaction, e);
            throw e;
        } catch (Exception e) {
            errorHandler.handleUnexpectedUnlockError(transaction, e);
        }
    }
}
