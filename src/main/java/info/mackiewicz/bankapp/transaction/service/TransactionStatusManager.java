package info.mackiewicz.bankapp.transaction.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component responsible for managing transaction statuses.
 * This class centralizes status update logic according to Single Responsibility Principle.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionStatusManager {
    
    private final TransactionService transactionService;
    
public boolean isStatusTransitionAllowed(TransactionStatus currentStatus, TransactionStatus newStatus) {
    return (currentStatus.isFinal() && !newStatus.equals(currentStatus));
}

@Transactional
public void setTransactionStatus(Transaction transaction, TransactionStatus status) {
    if (transaction.getStatus() == null || status == null) {
        throw new IllegalArgumentException("Transaction and status must not be null");
    }
    
    TransactionStatus currentStatus = transaction.getStatus();
    
    if (!isStatusTransitionAllowed(currentStatus, status)) {
        throw new IllegalStateException(
            "Cannot change transaction status from " + currentStatus + " to " + status);
    }
    
    log.debug("Setting transaction {} status to {}", transaction.getId(), status);
    transactionService.updateTransactionStatus(transaction, status);
}

    public boolean canBeProcessed(Transaction transaction) {
        TransactionStatus status = transaction.getStatus();
        return status == TransactionStatus.NEW;
    }
    
    public boolean isInProgress(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.PENDING;
    }
    
    public boolean isCompleted(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.DONE;
    }
    
    public boolean hasFailed(Transaction transaction) {
        return transaction.getStatus().isFailed();
    }
}