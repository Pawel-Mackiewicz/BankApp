package info.mackiewicz.bankapp.transaction.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.shared.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Facade service for transaction operations.
 * Delegates to specialized services for specific operation types:
 * - TransactionQueryService for read operations
 * - TransactionCommandService for write operations
 * - TransactionProcessingService for transaction processing
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionQueryService queryService;
    private final TransactionCommandService commandService;
    private final TransactionProcessingService processingService;

    /**
     * Saves a transaction in the system after validation.
     *
     * @param transaction the transaction to save
     * @return the saved transaction with generated ID
     * @throws IllegalArgumentException if the transaction fails validation
     */
    @Transactional
    public Transaction registerTransaction(Transaction transaction) {
        return commandService.registerTransaction(transaction);
    }

    /**
     * Deletes a transaction from the system by its ID.
     *
     * @param id the ID of the transaction to delete
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    public void deleteTransactionById(int id) {
        commandService.deleteTransactionById(id);
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id the ID of the transaction to retrieve
     * @return the transaction with the specified ID
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    public Transaction getTransactionById(int id) {
        return queryService.getTransactionById(id);
    }

    /**
     * Retrieves all transactions in the system.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return queryService.getAllTransactions();
    }

    /**
     * Retrieves all transactions with NEW status.
     *
     * @return a list of all transactions with NEW status
     */
    public List<Transaction> getAllNewTransactions() {
        return queryService.getAllNewTransactions();
    }

    /**
     * Retrieves all transactions for a specific account.
     *
     * @param accountId the ID of the account
     * @return a list of transactions for the specified account
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        return queryService.getTransactionsByAccountId(accountId);
    }

    /**
     * Retrieves the most recent transactions for a specific account.
     *
     * @param accountId the ID of the account
     * @param count the maximum number of transactions to retrieve
     * @return a list of the most recent transactions for the specified account
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
    public List<Transaction> getRecentTransactions(int accountId, int count) {
        return queryService.getRecentTransactions(accountId, count);
    }

    /**
     * Processes a single transaction by its ID.
     *
     * @param transactionId the ID of the transaction to process
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     * @throws IllegalArgumentException if the transaction fails validation
     */
    public void processTransactionById(int transactionId) {
        processingService.processTransactionById(transactionId);
    }

    /**
     * Processes all transactions with NEW status.
     * Failed transactions will be logged but won't stop the processing of remaining transactions.
     */
    public void processAllNewTransactions() {
        processingService.processAllNewTransactions();
    }
}
