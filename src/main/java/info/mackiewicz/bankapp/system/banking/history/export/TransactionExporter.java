package info.mackiewicz.bankapp.system.banking.history.export;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionExporter {
    String getFormat();
    ResponseEntity<byte[]> exportTransactions(List<Transaction> transactions);
}