package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    TransactionRepository transactionRepository;

    TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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


}
