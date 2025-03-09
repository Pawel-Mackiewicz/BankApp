package info.mackiewicz.bankapp.transaction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.shared.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for processing transactions.
 * Package-private to ensure access only through TransactionService facade.
 */
@Slf4j
@RequiredArgsConstructor
@Service
class TransactionProcessingService {
    private final TransactionProcessor processor;
    private final TransactionValidator validator;
    private final TransactionQueryService queryService;
    private final TransactionStatusManager statusManager;

    /**
     * Processes a transaction by its ID.
     * 
     * @param transactionId ID of the transaction to process
     * @throws TransactionNotFoundException if transaction is not found
     * @throws TransactionAlreadyProcessedException if transaction is already processed
     * @throws TransactionCannotBeProcessedException if transaction is faulty
     * @throws UnsupportedOperationException if transaction is in PENDING status
     * @throws IllegalArgumentException if transaction has invalid status
     */
    public void processTransactionById(int transactionId) {
        log.info("Processing single transaction: {}", transactionId);
        Transaction transaction = queryService.getTransactionById(transactionId);
        
        // Validate before processing
        validateTransaction(transaction);
        
        // Process based on status
        processBasedOnStatus(transaction);
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
     * Validates the transaction before processing.
     */
    private void validateTransaction(Transaction transaction) {
        try {
            validator.validate(transaction);
        } catch (Exception e) {
            log.error("Transaction validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Processes the transaction based on its current status.
     */
    private void processBasedOnStatus(Transaction transaction) {
        if (statusManager.canBeProcessed(transaction)) {
            processNewTransaction(transaction);
        } else if (statusManager.isInProgress(transaction)) {
            handlePendingTransaction(transaction);
        } else if (statusManager.isCompleted(transaction)) {
            handleDoneTransaction(transaction);
        } else if (statusManager.hasFailed(transaction)) {
            handleFaultyTransaction(transaction);
        } else {
            handleInvalidStatus(transaction);
        }
    }

    /**
     * Processes a transaction safely, catching and logging any exceptions.
     */
    private void processSafely(Transaction transaction) {
        try {
            validateTransaction(transaction);
            processNewTransaction(transaction);
        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", transaction.getId(), e.getMessage());
        }
    }

    private void handleDoneTransaction(Transaction transaction) {
        log.warn("Attempted to process already completed transaction: {}", transaction.getId());
        throw new TransactionAlreadyProcessedException(
            "Transaction " + transaction.getId() + " has already been processed");
    }

    private void handleFaultyTransaction(Transaction transaction) {
        log.error("Attempted to process faulty transaction: {}", transaction.getId());
        throw new TransactionCannotBeProcessedException(
            "Transaction " + transaction.getId() + " cannot be processed");
    }

    private void processNewTransaction(Transaction transaction) {
        log.info("Processing new transaction: {}", transaction.getId());
        processor.processTransaction(transaction);
    }

    private void handlePendingTransaction(Transaction transaction) {
        log.warn("Attempted to process PENDING transaction: {}", transaction.getId());
        throw new UnsupportedOperationException("Cannot process transaction in PENDING status: " + transaction.getId());
    }

    private void handleInvalidStatus(Transaction transaction) {
        log.error("Invalid transaction status encountered: {}", transaction.getStatus());
        throw new IllegalArgumentException("Unexpected transaction status: " + transaction.getStatus());
    }
}