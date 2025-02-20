package info.mackiewicz.bankapp.controller.api;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionFilterService;
import info.mackiewicz.bankapp.service.TransactionService;
import info.mackiewicz.bankapp.service.export.TransactionExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryRestController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final TransactionFilterService filterService;
    private final List<TransactionExporter> exporters;

    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        log.debug("Fetching transactions for user {} with filters", user.getUsername());

        List<Account> accounts = accountService.getAccountsByOwnersId(user.getId());
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account account : accounts) {
            allTransactions.addAll(transactionService.getRecentTransactions(account.getId(), 100));
        }

        List<Transaction> filteredTransactions = filterService.filterTransactions(
                allTransactions, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
        filterService.sortTransactions(filteredTransactions, sortBy, sortDirection);

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, filteredTransactions.size());
        
        if (start > filteredTransactions.size()) {
            log.debug("Requested page {} is beyond available data", page);
            return ResponseEntity.ok(new PageImpl<>(List.of(), PageRequest.of(page, size), filteredTransactions.size()));
        }

        List<Transaction> pageContent = filteredTransactions.subList(start, end);
        log.debug("Returning page {} with {} transactions", page, pageContent.size());

        return ResponseEntity.ok(new PageImpl<>(pageContent, PageRequest.of(page, size), filteredTransactions.size()));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "csv") String format
    ) {
        log.debug("Exporting transactions for user {} in {} format", user.getUsername(), format);

        List<Account> accounts = accountService.getAccountsByOwnersId(user.getId());
        List<Transaction> allTransactions = new ArrayList<>();
        for (Account account : accounts) {
            allTransactions.addAll(transactionService.getRecentTransactions(account.getId(), Integer.MAX_VALUE));
        }

        List<Transaction> filteredTransactions = filterService.filterTransactions(
                allTransactions, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);

        TransactionExporter exporter = exporters.stream()
                .filter(e -> e.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported export format: " + format));

        return exporter.exportTransactions(filteredTransactions);
    }
}