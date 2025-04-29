package info.mackiewicz.bankapp.core.transaction.service;

import info.mackiewicz.bankapp.core.transaction.exception.TransactionDeletionForbiddenException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.core.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for transaction creation and deletion operations.
 * Package-private to ensure access only through TransactionService facade.
 */
@Slf4j
@RequiredArgsConstructor
@Service
class TransactionCommandService {
    private final TransactionRepository repository;
    private final TransactionValidator validator;
    private final TransactionQueryService queryService;

    /**
     * Creates a new transaction in the system.
     * If the transaction is of type TRANSFER_OWN, it will be processed immediately.
     *
     * @param transaction the transaction to create
     *
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

        return savedTransaction;
    }


    /**
     * Deletes a transaction by its ID.
     *
     * The transaction can only be deleted if it has the status `NEW`.
     * If the status is not `NEW`, a {@code TransactionDeletionForbiddenException} will be thrown.
     * If the transaction does not exist, a {@code TransactionNotFoundException} will be thrown.
     *
     * @param id the ID of the transaction to delete
     * @throws TransactionNotFoundException if no transaction is found with the specified ID
     * @throws TransactionDeletionForbiddenException if the transaction status does not allow deletion
     */
    public void deleteTransactionById(int id) throws TransactionNotFoundException, TransactionDeletionForbiddenException {
        log.info("Attempting to delete transaction: {}", id);
        Transaction transaction = queryService.getTransactionById(id);
        if (isTransactionDeletable(transaction)) {
            repository.delete(transaction);
            log.info("Transaction {} deleted successfully", id);
        } else {
            String message = String.format("Transaction not deletable. Only transactions with status NEW can be deleted. " +
                    "Transaction Status: %s", transaction.getStatus());
            throw new TransactionDeletionForbiddenException(message);
        }
    }

    private boolean isTransactionDeletable(Transaction transaction) {
        //transactions without `status` are invalid so it can (and probably should) be deleted.
        return transaction.getStatus() == null || TransactionStatus.NEW.equals(transaction.getStatus());
    }
}