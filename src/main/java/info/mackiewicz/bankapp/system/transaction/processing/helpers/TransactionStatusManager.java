package info.mackiewicz.bankapp.system.transaction.processing.helpers;

import info.mackiewicz.bankapp.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Component responsible for managing transaction statuses in the database.
 * This class centralizes status update logic according to Single Responsibility Principle.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionStatusManager {
    
    private final TransactionRepository repository;
    
    /**
     * Checks if a status transition is allowed based on the state machine rules.
     * 
     * @param currentStatus the current status of the transaction
     * @param newStatus the target status for the transaction
     * @return true if the transition is valid, false otherwise
     */
    public boolean isStatusTransitionAllowed(TransactionStatus currentStatus, TransactionStatus newStatus) {
        return (!currentStatus.isFinal() && !newStatus.equals(currentStatus));
    }

    /**
     * Updates the status of a transaction in a thread-safe manner.
     * This method performs validation and delegates to database operations.
     *
     * @param transaction the transaction whose status needs to be updated
     * @param status the new status to set
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     * @throws IllegalArgumentException if transaction or status is null
     * @throws IllegalStateException if the status transition is not allowed
     */
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
        updateTransactionStatus(transaction, status);
    }

    /**
     * Updates only the status of a transaction in the database.
     * This method performs a direct database update without loading the entire entity.
     *
     * @param transaction the transaction whose status needs to be updated
     * @param status the new status to set
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    @Transactional
    private void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        int id = transaction.getId();
        log.debug("Updating status of transaction {} to {}", id, status);
        
        int updatedRows = repository.updateTransactionStatus(id, status);
        if (updatedRows == 0) {
            log.error("Failed to update status for transaction {}: transaction not found", id);
            throw new TransactionNotFoundException("Transaction with id " + id + " not found");
        }
        
        // Update the entity's status in memory (important for code that continues to use this entity)
        transaction.setStatus(status);
        log.debug("Transaction {} status updated to {}", id, status);
    }
}