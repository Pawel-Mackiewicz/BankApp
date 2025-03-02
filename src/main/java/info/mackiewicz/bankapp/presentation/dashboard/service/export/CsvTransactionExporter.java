package info.mackiewicz.bankapp.presentation.dashboard.service.export;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class CsvTransactionExporter implements TransactionExporter {
    
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String getFormat() {
        return "csv";
    }

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