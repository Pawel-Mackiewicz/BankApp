package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.*;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
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
    

    @Transactional(rollbackFor = Exception.class)
    public Transaction createTransaction(Transaction transaction) {
        log.debug("AccountService.createTransaction invoked with: {}", transaction);
        
        Transaction savedTransaction = repository.save(transaction);
        log.debug("Transaction saved with ID: {}", savedTransaction.getId());
        
        processOwnTransfer(savedTransaction);
        
        return savedTransaction;
    }

    private void processOwnTransfer(Transaction savedTransaction) {
        log.debug("Checking if transaction {} is an own transfer", savedTransaction.getId());
        if (TransactionType.TRANSFER_OWN.equals(savedTransaction.getType())) {
            log.info("Processing own transfer transaction: {}", savedTransaction.getId());
            processTransaction(savedTransaction);
        } else {
            log.debug("Transaction {} is not an own transfer", savedTransaction.getId());
        }
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

    // *********** TRANSACTION PROCESSING ************
    public void processTransactionById(int transactionId) {
        log.info("Processing single transaction: {}", transactionId);
        Transaction transaction = getTransactionById(transactionId);
        processTransaction(transaction);
    }

    public void processAllNewTransactions() {
        log.info("Starting batch processing of new transactions");
        List<Transaction> transactions = getAllNewTransactions();
        log.debug("Found {} new transactions to process", transactions.size());
        transactions.forEach(this::processTransaction);
        log.info("Completed batch processing of {} transactions", transactions.size());
    }

    private void processTransaction(Transaction transaction) {
        log.debug("Processing transaction: {} with status: {}", transaction.getId(), transaction.getStatus());
        
        switch (transaction.getStatus()) {
            case DONE -> {
                log.warn("Attempted to process already completed transaction: {}", transaction.getId());
                throw new TransactionAlreadyProcessedException(
                        "Transaction " + transaction.getId() + " has already been processed");
            }
            case FAULTY -> {
                log.error("Attempted to process faulty transaction: {}", transaction.getId());
                throw new TransactionCannotBeProcessedException(
                        "Transaction " + transaction.getId() + " cannot be processed");
            }
            case NEW -> {
                log.info("Processing new transaction: {}", transaction.getId());
                processor.processTransaction(transaction);
            }
            case PENDING -> {
                log.warn("Attempted to process PENDING transaction: {}", transaction.getId());
                throw new UnsupportedOperationException("Unimplemented case: " + transaction.getStatus());
            }
            default -> {
                log.error("Invalid transaction status encountered: {}", transaction.getStatus());
                throw new IllegalArgumentException("Unexpected value: " + transaction.getStatus());
            }
        }
    }
}
