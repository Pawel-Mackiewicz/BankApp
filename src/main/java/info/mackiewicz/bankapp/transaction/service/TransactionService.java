package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.*;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final TransactionProcessor processor;
    private final AccountService accountService;
    private final TransactionValidator validator;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        log.debug("Creating new transaction: {}", transaction);
        
        // Validate before saving
        validator.validate(transaction);
        
        // Save to repository
        Transaction savedTransaction = repository.save(transaction);
        log.debug("Transaction saved with ID: {}", savedTransaction.getId());
        
        // Process immediately if it's an own transfer
        if (TransactionType.TRANSFER_OWN.equals(savedTransaction.getType())) {
            log.info("Processing own transfer transaction: {}", savedTransaction.getId());
            processor.processTransaction(savedTransaction);
        }
        
        return savedTransaction;
    }

    public void deleteTransactionById(int id) {
        log.info("Attempting to delete transaction: {}", id);
        Transaction transaction = getTransactionById(id);
        repository.delete(transaction);
        log.info("Transaction {} deleted successfully", id);
    }

    public Transaction getTransactionById(int id) {
        log.debug("Finding transaction by ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transaction not found: {}", id);
                    return new TransactionNotFoundException("Transaction " + id + " not found");
                });
    }

    public List<Transaction> getAllTransactions() {
        log.debug("Retrieving all transactions");
        List<Transaction> transactions = repository.findAll();
        log.debug("Found {} transactions", transactions.size());
        return transactions;
    }

    public List<Transaction> getAllNewTransactions() {
        log.debug("Retrieving all NEW status transactions");
        List<Transaction> transactions = new CopyOnWriteArrayList<>(repository.findByStatus(TransactionStatus.NEW));
        log.debug("Found {} new transactions", transactions.size());
        return transactions;
    }

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

    public List<Transaction> getRecentTransactions(int accountId, int count) {
        log.debug("Retrieving {} most recent transactions for account: {}", count, accountId);
        return repository.findTopNByAccountIdOrderByCreatedDesc(accountId, count)
                .orElseThrow(() -> {
                    log.warn("No transactions found for account: {}", accountId);
                    return new NoTransactionsForAccountException(
                            "Account " + accountId + " did not make any transactions");
                });
    }

    public void processTransactionById(int transactionId) {
        log.info("Processing single transaction: {}", transactionId);
        Transaction transaction = getTransactionById(transactionId);
        
        // Validate before processing
        validator.validate(transaction);
        
        switch (transaction.getStatus()) {
            case DONE -> {
                log.warn("Attempted to process already completed transaction: {}", transactionId);
                throw new TransactionAlreadyProcessedException(
                    "Transaction " + transactionId + " has already been processed");
            }
            case FAULTY -> {
                log.error("Attempted to process faulty transaction: {}", transactionId);
                throw new TransactionCannotBeProcessedException(
                    "Transaction " + transactionId + " cannot be processed");
            }
            case NEW -> {
                log.info("Processing new transaction: {}", transactionId);
                processor.processTransaction(transaction);
            }
            case PENDING -> {
                log.warn("Attempted to process PENDING transaction: {}", transactionId);
                throw new UnsupportedOperationException("Unimplemented case: " + transaction.getStatus());
            }
            default -> {
                log.error("Invalid transaction status encountered: {}", transaction.getStatus());
                throw new IllegalArgumentException("Unexpected value: " + transaction.getStatus());
            }
        }
    }

    public void processAllNewTransactions() {
        log.info("Starting batch processing of new transactions");
        List<Transaction> transactions = getAllNewTransactions();
        log.debug("Found {} new transactions to process", transactions.size());
        
        for (Transaction transaction : transactions) {
            try {
                validator.validate(transaction);
                processor.processTransaction(transaction);
            } catch (Exception e) {
                log.error("Failed to process transaction {}: {}", transaction.getId(), e.getMessage());
            }
        }
        
        log.info("Completed batch processing of {} transactions", transactions.size());
    }
}
