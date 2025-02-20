package info.mackiewicz.bankapp.service.export;

import info.mackiewicz.bankapp.model.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionExporter {
    String getFormat();
    ResponseEntity<byte[]> exportTransactions(List<Transaction> transactions);
}