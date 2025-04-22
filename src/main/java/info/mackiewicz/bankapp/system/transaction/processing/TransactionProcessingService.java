package info.mackiewicz.bankapp.system.transaction.processing;

import info.mackiewicz.bankapp.system.transaction.processing.core.TransactionProcessor;
import info.mackiewicz.bankapp.system.transaction.processing.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.system.transaction.processing.helpers.TransactionStatusChecker;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionAccountConflictException;
import info.mackiewicz.bankapp.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for processing transactions.
 * Package-private to ensure access only through TransactionService facade.
 * Centralizes all transaction processing decisions and error handling.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionProcessingService {
    private final TransactionService transactionService;
    private final TransactionProcessor processor;
    private final TransactionValidator validator;
    private final TransactionStatusChecker statusChecker;
    private final TransactionErrorHandler errorHandler;

    /**
     * Processes a transaction by its ID.
     * 
     * @param transactionId ID of the transaction to process
     * @throws TransactionNotFoundException if transaction is not found
     */
    public void processTransactionById(int transactionId) {
        log.info("Processing single transaction: {}", transactionId);
        Transaction transaction = transactionService.getTransactionById(transactionId);
        processSafely(transaction);
    }

    /**
     * Processes a single transaction.
     * 
     * @param transaction the transaction to process
     */
    public void processTransaction(Transaction transaction) {
        log.info("Processing single transaction: {}", transaction.getId());
        processSafely(transaction);
    }

    /**
     * Processes all transactions with NEW status.
     * Failed transactions will be logged but won't stop the processing of remaining transactions.
     */
    public void processAllNewTransactions() {
        log.info("Starting batch processing of new transactions");
        List<Transaction> transactions = transactionService.getAllNewTransactions();
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
            validator.validate(transaction);

            // Status validation
            statusChecker.validateForProcessing(transaction);

            // Process the transaction
            executeTransaction(transaction);
        } catch (TransactionValidationException e) {
            errorHandler.handleValidationError(transaction, e);
        } catch (TransactionAccountConflictException e) {
            errorHandler.handleValidationError(transaction, e);
        } catch (InsufficientFundsException e) {
            errorHandler.handleInsufficientFundsError(transaction, e);
        } catch (Exception e) {
            errorHandler.handleUnexpectedError(transaction, e);
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