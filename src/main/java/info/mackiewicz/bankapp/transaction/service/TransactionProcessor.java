package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing financial transactions with proper locking and validation mechanisms.
 */
@RequiredArgsConstructor
@Service
public class TransactionProcessor {

    private final StrategyResolver strategyResolver;
    private final TransactionRepository repository;
    private final AccountLockManager accountLockManager;
    private final TransactionValidator validator;

    /**
     * Asynchronously processes a financial transaction with proper account locking and error handling.
     * The method ensures that accounts are properly locked during the transaction and unlocked afterward,
     * even if an error occurs during processing.
     *
     * @param transaction The transaction to be processed
     */
    @Async
    public void processTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);
        acquireAccountLocks(transaction);
        try {
            executeTransactionProcess(transaction);
        } catch (InsufficientFundsException e) {
            handleInsufficientFundsError(transaction, e);
        } catch (TransactionValidationException e) {
            handleValidationError(transaction, e);
        } catch (Exception e) {
            handleUnexpectedError(transaction, e);
        } finally {
            releaseAccountLocks(transaction);
        }
    }

    /**
     * Acquires locks on both source and destination accounts to ensure transaction atomicity.
     */
    private void acquireAccountLocks(Transaction transaction) {
        accountLockManager.lockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    /**
     * Executes the transaction process including validation and strategy execution.
     */
    private void executeTransactionProcess(Transaction transaction) {
        validateAndInitialize(transaction);
        executeTransactionStrategy(transaction);
    }

    /**
     * Validates the transaction and sets its initial status.
     */
    private void validateAndInitialize(Transaction transaction) {
        validator.validate(transaction);
        setTransactionStatus(transaction, TransactionStatus.PENDING);
    }

    /**
     * Executes the appropriate transaction strategy and handles the result.
     */
    private void executeTransactionStrategy(Transaction transaction) {
        TransactionStrategy strategy = strategyResolver.resolveStrategy(transaction);
        boolean success = strategy.execute(transaction);
        
        if (success) {
            LoggingService.logSuccessfulTransaction(transaction);
            setTransactionStatus(transaction, TransactionStatus.DONE);
        } else {
            LoggingService.logErrorInMakingTransaction(transaction);
            setTransactionStatus(transaction, TransactionStatus.FAULTY);
            throw new RuntimeException("Transaction execution failed");
        }
    }

    /**
     * Handles insufficient funds error during transaction processing.
     */
    private void handleInsufficientFundsError(Transaction transaction, InsufficientFundsException e) {
        LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
        setTransactionStatus(transaction, TransactionStatus.FAULTY);
        throw e;
    }

    /**
     * Handles validation errors during transaction processing.
     */
    private void handleValidationError(Transaction transaction, TransactionValidationException e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Validation Error: " + e.getMessage());
        setTransactionStatus(transaction, TransactionStatus.FAULTY);
        throw e;
    }

    /**
     * Handles unexpected errors during transaction processing.
     */
    private void handleUnexpectedError(Transaction transaction, Exception e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Unexpected Error: " + e.getMessage());
        setTransactionStatus(transaction, TransactionStatus.FAULTY);
        throw new RuntimeException("Unexpected error during transaction processing", e);
    }

    /**
     * Releases locks on accounts after transaction processing is complete.
     */
    private void releaseAccountLocks(Transaction transaction) {
        accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }

    /**
     * Updates and persists the transaction status.
     */
    private void setTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
