package info.mackiewicz.bankapp.system.banking.history.export;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Defines the contract for exporting financial transactions in various formats.
 * Implementations of this interface are responsible for converting transactions
 * into a specific format (e.g., CSV, PDF) and generating an appropriate file
 * for download.
 */
public interface TransactionExporter {
    String getFormat();
    ResponseEntity<byte[]> exportTransactions(List<Transaction> transactions);
}