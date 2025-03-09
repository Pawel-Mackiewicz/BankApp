package info.mackiewicz.bankapp.transaction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.error.TransactionFailureHandler;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for processing transactions.
 * Package-private to ensure access only through TransactionService facade.
 * Centralizes all transaction processing decisions and error handling.
 */
@Slf4j
@RequiredArgsConstructor
@Service
class TransactionProcessingService {
    private final TransactionProcessor processor;
    private final TransactionValidator validator;
    private final TransactionQueryService queryService;
    private final TransactionStatusChecker statusChecker;
    private final TransactionFailureHandler errorHandler;

    /**
     * Processes a transaction by its ID.
     * 
     * @param transactionId ID of the transaction to process
     * @throws TransactionNotFoundException if transaction is not found
     */
    public void processTransactionById(int transactionId) {
        log.info("Processing single transaction: {}", transactionId);
        Transaction transaction = queryService.getTransactionById(transactionId);
        processSafely(transaction);
    }

    /**
     * Processes all transactions with NEW status.
     * Failed transactions will be logged but won't stop the processing of remaining transactions.
     */
    public void processAllNewTransactions() {
        log.info("Starting batch processing of new transactions");
        List<Transaction> transactions = queryService.getAllNewTransactions();
        log.debug("Found {} new transactions to process", transactions.size());
        
        transactions.forEach(this::processSafely);
        
        log.info("Completed batch processing of {} transactions", transactions.size());
    }

    /**
     * Processes a transaction safely, catching and handling all types of exceptions.
     * Centralizes all error handling using TransactionErrorHandler.
     */
    private void processSafely(Transaction transaction) {
        try {
            // Initial validation
            validateTransaction(transaction);
            
            // Status validation
            statusChecker.validateForProcessing(transaction);
            
            // Process the transaction
            executeTransaction(transaction);
        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", transaction.getId(), e.getMessage());
            // General catch for any unhandled exceptions
            if (!(e instanceof TransactionValidationException)) {
                errorHandler.handleUnexpectedError(transaction, e);
            }
        }
    }

    /**
     * Validates the transaction before processing.
     * 
     * @throws TransactionValidationException if validation fails
     */
    private void validateTransaction(Transaction transaction) {
        try {
            validator.validate(transaction);
        } catch (TransactionValidationException e) {
            errorHandler.handleValidationError(transaction, e);
        }
    }

    /**
     * Executes a transaction with proper error handling.
     * Handles all specific exceptions that may occur during transaction processing.
     */
    private void executeTransaction(Transaction transaction) {
            log.info("Processing transaction: {}", transaction.getId());
            processor.processTransaction(transaction);
    }
}