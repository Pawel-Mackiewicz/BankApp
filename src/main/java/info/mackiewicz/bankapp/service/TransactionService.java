package info.mackiewicz.bankapp.service;

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

    TransactionService(TransactionRepository repository, TransactionProcessor processor) {
        this.repository = repository;
        this.processor = processor;
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
                .orElseThrow(() -> new RuntimeException("Transaction " + id + " not found"));
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public List<Transaction> getAllNewTransactions() {
        return repository.findByStatus(TransactionStatus.NEW);
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        return repository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account " + accountId + " didn't made any transactions or don't exist"));
    }

    //***********TRANSACTION PROCESSING************
    @Async
    public void processTransactionById(int transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        processTransaction(transaction);
    }

    @Async
    public void processAllNewTransactions() {
        List<Transaction> transactions = getAllNewTransactions();
        transactions.forEach(
                this::processTransaction
        );
    }

    private void processTransaction(Transaction transaction) {
        switch (transaction.getStatus()) {
            case DONE -> throw new RuntimeException("Transaction " + transaction.getId() + " has already been processed");
            case FAULTY -> throw new RuntimeException("Transaction " + transaction.getId() + " cannot be processed");
            case NEW -> processor.processTransaction(transaction);
        }
    }
}