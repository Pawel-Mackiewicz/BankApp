package info.mackiewicz.bankapp.core.transaction.service;

import info.mackiewicz.bankapp.core.transaction.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * Validate and register a new transaction in the system.
     *
     * @param transaction the transaction to save
     * @return the saved transaction with generated ID
     * @throws TransactionValidationException if the transaction fails validation
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
}
