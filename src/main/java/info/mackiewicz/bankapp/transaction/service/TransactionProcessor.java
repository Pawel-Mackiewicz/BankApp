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
        setTransactionStatus(transaction, TransactionStatus.PENDING);
    }

    private void executeTransactionStrategy(Transaction transaction) {
        TransactionStrategy strategy = strategyResolver.resolveStrategy(transaction);
        boolean success = strategy.execute(transaction);
        
        if (success) {
            LoggingService.logSuccessfulTransaction(transaction);
            setTransactionStatus(transaction, TransactionStatus.DONE);
        } else {
            LoggingService.logErrorInMakingTransaction(transaction);
            setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
            throw new RuntimeException("Transaction execution failed");
        }
    }

    private void handleInsufficientFundsError(Transaction transaction, InsufficientFundsException e) {
        LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
        setTransactionStatus(transaction, TransactionStatus.INSUFFICIENT_FUNDS);
        throw e;
    }

    private void handleValidationError(Transaction transaction, TransactionValidationException e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Validation Error: " + e.getMessage());
        setTransactionStatus(transaction, TransactionStatus.VALIDATION_ERROR);
        throw e;
    }

    private void handleUnexpectedError(Transaction transaction, Exception e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Unexpected Error: " + e.getMessage());
        setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        throw new RuntimeException("Unexpected error during transaction processing", e);
    }

    private void releaseAccountLocks(Transaction transaction) {
        accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }

    private void setTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
