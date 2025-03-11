package info.mackiewicz.bankapp.transaction.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for querying transactions.
 * Package-private to ensure access only through TransactionService facade.
 */
@Slf4j
@RequiredArgsConstructor
@Service
class TransactionQueryService {
    private final TransactionRepository repository;
    private final AccountService accountService;

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id the ID of the transaction to retrieve
     * @return the transaction with the specified ID
     * @throws TransactionNotFoundException if no transaction is found with the given ID
     */
    public Transaction getTransactionById(int id) {
        log.debug("Finding transaction by ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transaction not found: {}", id);
                    return new TransactionNotFoundException("Transaction " + id + " not found");
                });
    }

    /**
     * Retrieves all transactions in the system.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        log.debug("Retrieving all transactions");
        List<Transaction> transactions = repository.findAll();
        log.debug("Found {} transactions", transactions.size());
        return transactions;
    }

    /**
     * Retrieves all transactions with NEW status.
     * Returns a new ArrayList to prevent concurrent modification issues.
     *
     * @return a list of all transactions with NEW status
     */
    public List<Transaction> getAllNewTransactions() {
        log.debug("Retrieving all NEW status transactions");
        List<Transaction> transactions = new ArrayList<>(repository.findByStatus(TransactionStatus.NEW));
        log.debug("Found {} new transactions", transactions.size());
        return transactions;
    }

    /**
     * Retrieves all transactions for a specific account.
     *
     * @param accountId the ID of the account
     * @return a list of transactions for the specified account
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        log.debug("Finding transactions for account: {}", accountId);
        
        // Verify account exists
        Account account = accountService.getAccountById(accountId);
        log.debug("Account {} verified", account.getId());

        return repository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.warn("No transactions found for account: {}", accountId);
                    return new NoTransactionsForAccountException(
                            "Account " + accountId + " did not make any transactions");
                });
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
        log.debug("Retrieving {} most recent transactions for account: {}", count, accountId);
        return repository.findTopNByAccountIdOrderByCreatedDesc(accountId, count)
                .orElseThrow(() -> {
                    log.warn("No transactions found for account: {}", accountId);
                    return new NoTransactionsForAccountException(
                            "Account " + accountId + " did not make any transactions");
                });
    }
}