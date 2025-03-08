package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionProcessor {

    private final StrategyResolver strategyResolver;
    private final TransactionRepository repository;
    private final AccountLockManager accountLockManager;
    private final TransactionValidator validator;

    @Async
    public void processTransaction(Transaction transaction) {
        lockAndLogAccounts(transaction);
        try {
            attemptTransaction(transaction);
        } finally {
            unlockAndLogAccounts(transaction);
        }
    }

    private void lockAndLogAccounts(Transaction transaction) {
        accountLockManager.lockAccounts(transaction.getDestinationAccount(), transaction.getSourceAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    private void attemptTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);

        try {
            // Validate before processing
            validator.validate(transaction);
            
            // Change status to pending
            changeTransactionStatus(transaction, TransactionStatus.PENDING);
            
            // Get appropriate strategy and execute
            TransactionStrategy strategy = strategyResolver.resolveStrategy(transaction);
            boolean success = strategy.execute(transaction);
            
            if (success) {
                LoggingService.logSuccessfulTransaction(transaction);
                changeTransactionStatus(transaction, TransactionStatus.DONE);
            } else {
                LoggingService.logErrorInMakingTransaction(transaction);
                changeTransactionStatus(transaction, TransactionStatus.FAULTY);
            }
        } catch (Exception e) {
            LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
            changeTransactionStatus(transaction, TransactionStatus.FAULTY);
            throw e;
        }
    }

    private void unlockAndLogAccounts(Transaction transaction) {
        accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }

    private void changeTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
