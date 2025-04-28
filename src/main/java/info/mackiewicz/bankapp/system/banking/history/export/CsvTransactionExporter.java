package info.mackiewicz.bankapp.system.banking.history.export;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * A service implementation for exporting financial transactions in CSV format.
 * This class generates a CSV file from a list of transactions and prepares it
 * for download via an HTTP response.
 */
@Service
public class CsvTransactionExporter implements TransactionExporter {
    
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String getFormat() {
        return "csv";
    }

    /**
     * Exports a list of financial transactions in CSV format and returns the resulting file
     * as an HTTP response entity.
     *
     * @param transactions the list of transactions to be exported; each transaction contains details such as date, amount, type, accounts involved, title, and status
     * @return a ResponseEntity containing the generated CSV file as a byte array, with appropriate HTTP headers set for file download
     */
    @Override
    public ResponseEntity<byte[]> exportTransactions(List<Transaction> transactions) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Amount,Type,From Account,To Account,Title,Status\n");

        for (Transaction t : transactions) {
            csv.append(String.format("%s,%s,%s,%s,%s,\"%s\",%s\n",
                    t.getDate().format(CSV_DATE_FORMATTER),
                    t.getAmount(),
                    t.getType(),
                    getAccountId(t.getSourceAccount()),
                    getAccountId(t.getDestinationAccount()),
                    escapeCSV(t.getTitle()),
                    t.getStatus()
            ));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "transactions.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String getAccountId(Account account) {
        return Optional.ofNullable(account)
                .map(Account::getId)
                .map(String::valueOf)
                .orElse("");
    }

    private String escapeCSV(String text) {
        return text.replace("\"", "\"\"");
    }
}