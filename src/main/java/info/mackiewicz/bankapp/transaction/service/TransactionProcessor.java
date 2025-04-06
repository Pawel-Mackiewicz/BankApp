package info.mackiewicz.bankapp.transaction.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.account.exception.AccountValidationException;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.infrastructure.logging.LoggingService;
import info.mackiewicz.bankapp.system.locking.AccountLockManager;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionBaseException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutor;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutorRegistry;
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
            } catch (TransactionBaseException e) {
                throw e;
            } catch (Exception e) {
                errorHandler.handleUnexpectedError(transaction, e);
                throw new TransactionExecutionException(
                    String.format("Unexpected error during transaction %d processing", transaction.getId()), e);
            }
        } catch (AccountLockException e) {
            errorHandler.handleLockError(transaction, e);
        } catch (Exception e) {
            if (e instanceof TransactionBaseException) {
                throw e;
            }
            errorHandler.handleUnexpectedLockError(transaction, e);
            throw new TransactionExecutionException("Unexpected lock error for transaction %d" + transaction.getId(), e);
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
        } catch (Exception e) {
            errorHandler.handleTransactionStatusChangeError(transaction, e);
            throw new TransactionExecutionException("Error while changing transaction status", e);
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
            throw new TransactionValidationException("Validation error for transaction " + transaction.getId(), e);
        } catch (InsufficientFundsException e) {
            errorHandler.handleInsufficientFundsError(transaction, e);
            throw e;
        } catch (Exception e) {
            errorHandler.handleUnexpectedError(transaction, e);
            throw new TransactionExecutionException("Unexpected error during transaction processing", e);
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
            throw new TransactionExecutionException("Unexpected unlock error for transaction " + transaction.getId(),
                    e);

        }
    }
}
