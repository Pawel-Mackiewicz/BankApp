package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionStatus;
import info.mackiewicz.bankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionProcessingService processingService;

    TransactionService(TransactionRepository transactionRepository, TransactionProcessingService transactionProcessingService) {
        this.transactionRepository = transactionRepository;
        this.processingService = transactionProcessingService;
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransactionById(int id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction " + id + " not found"));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getAllNewTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.NEW);
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        return transactionRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account " + accountId + " didn't made any transactions or don't exist"));
    }

    public void changeTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }


    //***********TRANSACTION PROCESSING************
    public void processTransactionById(int transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        processTransaction(transaction);
    }

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
            case NEW -> {
                processingService.processTransaction(transaction);
            }
        }
    }
}