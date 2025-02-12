package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.*;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionStatus;
import info.mackiewicz.bankapp.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final TransactionProcessor processor;
    private final AccountService accountService;

    TransactionService(TransactionRepository repository, TransactionProcessor processor, AccountService accountService) {
        this.repository = repository;
        this.processor = processor;
        this.accountService = accountService;
    }

    public Transaction createTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    public void deleteTransactionById(int id) {
        Transaction transaction = getTransactionById(id);
        repository.delete(transaction);
    }

    public Transaction getTransactionById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction " + id + " not found"));
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public List<Transaction> getAllNewTransactions() {
        return repository.findByStatus(TransactionStatus.NEW);
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        //throws exception, if account does not exist
        @SuppressWarnings("unused")
        Account account = accountService.getAccountById(accountId);

            return repository.findByAccountId(accountId)
                    .orElseThrow(() -> new NoTransactionsForAccountException("Account " + accountId + " did not make any transactions"));
    }

    public List<Transaction> getRecentTransactions(int accountId, int count) {
        return repository.findTopNByAccountIdOrderByCreatedDesc(accountId, count)
                .orElseThrow(() -> new NoTransactionsForAccountException("Account " + accountId + " did not make any transactions"));
    }

    //*********** TRANSACTION PROCESSING ************
    @Async
    public void processTransactionById(int transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        processTransaction(transaction);
    }

    @Async
    public void processAllNewTransactions() {
        List<Transaction> transactions = getAllNewTransactions();
        transactions.forEach(this::processTransaction);
    }

    private void processTransaction(Transaction transaction) {
        switch (transaction.getStatus()) {
                    case DONE -> throw new TransactionAlreadyProcessedException("Transaction " + transaction.getId() + " has already been processed");
                    case FAULTY -> throw new TransactionCannotBeProcessedException("Transaction " + transaction.getId() + " cannot be processed");
                    case NEW -> processor.processTransaction(transaction);
                    case IN_PROGRESS -> throw new UnsupportedOperationException("Unimplemented case: " + transaction.getStatus());
                    default -> throw new IllegalArgumentException("Unexpected value: " + transaction.getStatus());
        }
    }
}
