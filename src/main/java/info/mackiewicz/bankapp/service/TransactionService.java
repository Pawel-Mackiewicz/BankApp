package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Transaction;
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

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction " + id + " not found"));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        return transactionRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account " + accountId + " didn't made any transactions or don't exist"));
    }

    public void processTransactionById(int transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        processTransaction(transaction);
    }

    public void processAllTransactions(List<Transaction> transactions) {
        transactions.forEach(
                this::processTransaction
        );
    }

    private void processTransaction(Transaction transaction) {
        //TODO: tu zrobić boolean tzn process() ma być boolean i na podstawie tego będziemy dalej działać z daną transakcją.
        processingService.processTransaction(transaction);
    }

}
