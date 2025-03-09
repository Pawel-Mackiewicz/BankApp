package info.mackiewicz.bankapp.transaction.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for processing financial transactions with proper locking and validation mechanisms.
 */
@RequiredArgsConstructor
@Service
public class TransactionProcessor {

    private final StrategyResolver strategyResolver;
    private final AccountLockManager accountLockManager;
    private final TransactionValidator validator;
    private final TransactionErrorHandler errorHandler;
    private final TransactionStatusManager statusManager;

    /**
     * Asynchronously processes a financial transaction with proper account locking and error handling.
     * Errors are handled by the TransactionErrorHandler and propagated to registered observers.
     */
    @Async
    public void processTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);
        try {
            acquireAccountLocks(transaction);
            try {
                executeTransactionProcess(transaction);
                LoggingService.logSuccessfulTransaction(transaction);
                statusManager.setTransactionStatus(transaction, TransactionStatus.DONE);
            } catch (InsufficientFundsException e) {
                errorHandler.handleInsufficientFundsError(transaction, e);
            } catch (TransactionValidationException e) {
                errorHandler.handleValidationError(transaction, e);
            } catch (TransactionExecutionException e) {
                errorHandler.handleExecutionError(transaction, e);
            } catch (Exception e) {
                errorHandler.handleUnexpectedError(transaction, e);
            }
        } catch (AccountLockException e) {
            // Explicitly handle lock acquisition failures
            errorHandler.handleLockError(transaction, e);
        } catch (Exception e) {
            // Handle any other unexpected errors during lock acquisition
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

    private void acquireAccountLocks(Transaction transaction) {
        accountLockManager.lockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    private void executeTransactionProcess(Transaction transaction) {
        validateAndInitialize(transaction);
        executeTransactionStrategy(transaction);
    }

    private void validateAndInitialize(Transaction transaction) {
        validator.validate(transaction);
        statusManager.setTransactionStatus(transaction, TransactionStatus.PENDING);
    }

    private void executeTransactionStrategy(Transaction transaction) {
        TransactionStrategy strategy = strategyResolver.resolveStrategy(transaction);
        strategy.execute(transaction);

    }

    private void releaseAccountLocks(Transaction transaction) {
            accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
            LoggingService.logUnlockingAccounts(transaction);
    }
}
