package info.mackiewicz.bankapp.presentation.dashboard.service.export;

import org.springframework.http.ResponseEntity;

import info.mackiewicz.bankapp.transaction.model.Transaction;

import java.util.List;

public interface TransactionExporter {
    String getFormat();
    ResponseEntity<byte[]> exportTransactions(List<Transaction> transactions);
}