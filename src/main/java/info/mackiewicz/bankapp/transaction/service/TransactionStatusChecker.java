package info.mackiewicz.bankapp.transaction.service;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.shared.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component responsible for checking transaction status and determining if a transaction
 * can be processed based on its current state.
 */
@Slf4j
@RequiredArgsConstructor
@Component
class TransactionStatusChecker {
    
    /**
     * Checks if transaction can be processed based on its status.
     * 
     * @param transaction The transaction to check
     * @return true if the transaction can be processed, false otherwise
     */
    public boolean canBeProcessed(Transaction transaction) {
        TransactionStatus status = transaction.getStatus();
        return status == TransactionStatus.NEW;
    }
    
    /**
     * Checks if the transaction is currently in progress (being processed).
     * 
     * @param transaction The transaction to check
     * @return true if the transaction is in progress
     */
    public boolean isInProgress(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.PENDING;
    }
    
    /**
     * Checks if the transaction has completed processing successfully.
     * 
     * @param transaction The transaction to check
     * @return true if the transaction is completed
     */
    public boolean isCompleted(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.DONE;
    }
    
    /**
     * Checks if the transaction has failed.
     * 
     * @param transaction The transaction to check
     * @return true if the transaction has failed
     */
    public boolean hasFailed(Transaction transaction) {
        return transaction.getStatus().isFailed();
    }
    
    /**
     * Validates if a transaction can be processed and throws appropriate exception if not.
     * 
     * @param transaction The transaction to validate
     * @throws TransactionAlreadyProcessedException if transaction is already processed
     * @throws TransactionCannotBeProcessedException if transaction is faulty
     * @throws UnsupportedOperationException if transaction is in PENDING status
     */
    public void validateForProcessing(Transaction transaction) {
        if (isCompleted(transaction)) {
            log.warn("Attempted to process already completed transaction: {}", transaction.getId());
            throw new TransactionAlreadyProcessedException(
                "Transaction " + transaction.getId() + " has already been processed");
        } else if (isInProgress(transaction)) {
            log.warn("Attempted to process PENDING transaction: {}", transaction.getId());
            throw new UnsupportedOperationException("Cannot process transaction in PENDING status: " + transaction.getId());
        } else if (hasFailed(transaction)) {
            log.error("Attempted to process faulty transaction: {}", transaction.getId());
            throw new TransactionCannotBeProcessedException(
                "Transaction " + transaction.getId() + " cannot be processed");
        } else if (!canBeProcessed(transaction)) {
            log.error("Invalid transaction status encountered: {}", transaction.getStatus());
            throw new IllegalArgumentException("Unexpected transaction status: " + transaction.getStatus());
        }
        
        // Transaction can be processed
        log.debug("Transaction {} status check passed: {}", transaction.getId(), transaction.getStatus());
    }
}