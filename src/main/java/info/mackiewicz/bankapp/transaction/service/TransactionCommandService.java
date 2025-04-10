package info.mackiewicz.bankapp.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for transaction creation and deletion operations.
 * Package-private to ensure access only through TransactionService facade.
 */
@Slf4j
@RequiredArgsConstructor
@Service
class TransactionCommandService {
    private final TransactionRepository repository;
    private final TransactionProcessingService processingService;
    private final TransactionValidator validator;
    private final TransactionQueryService queryService;

    /**
     * Creates a new transaction in the system.
     * If the transaction is of type TRANSFER_OWN, it will be processed immediately.
     *
     * @param transaction the transaction to create
     * @return the saved transaction with generated ID
     * @throws TransactionValidationException if the transaction fails validation
     */
    @Transactional
    public Transaction registerTransaction(Transaction transaction) {
        log.debug("Creating new transaction: {}", transaction);
        
        // Validate before saving
        validator.validate(transaction);
        
        // Save to repository
        Transaction savedTransaction = repository.save(transaction);
        log.debug("Transaction saved with ID: {}", savedTransaction.getId());
        
        // Process immediately if it's an own transfer
        if (TransactionType.TRANSFER_OWN.equals(savedTransaction.getType())) {
            log.info("Processing own transfer transaction: {}", savedTransaction.getId());
            processingService.processTransaction(savedTransaction);
        }
        
        return savedTransaction;
    }

    /**
     * Deletes a transaction from the system by its ID.
     *
     * @param id the ID of the transaction to delete
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    public void deleteTransactionById(int id) {
        log.info("Attempting to delete transaction: {}", id);
        Transaction transaction = queryService.getTransactionById(id);
        repository.delete(transaction);
        log.info("Transaction {} deleted successfully", id);
    }
}