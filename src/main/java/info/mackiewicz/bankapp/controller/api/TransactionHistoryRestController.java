package info.mackiewicz.bankapp.controller.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionFilterService;
import info.mackiewicz.bankapp.service.TransactionService;
import info.mackiewicz.bankapp.service.export.TransactionExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
            @RequestParam Integer accountId,
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
        log.debug("Fetching transactions for account {} (user: {})", accountId, user.getUsername());

        validateAccountOwnership(user, accountId);

        List<Transaction> transactions = fetchAndFilterTransactions(
            accountId, dateFrom, dateTo, type, 
            amountFrom, amountTo, searchQuery, 
            sortBy, sortDirection
        );

        return createPaginatedResponse(transactions, page, size);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam Integer accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "csv") String format
    ) {
        log.debug("Exporting transactions for account {} (user: {}) in {} format", 
            accountId, user.getUsername(), format);

        validateAccountOwnership(user, accountId);

        List<Transaction> transactions = fetchAndFilterTransactions(
            accountId, dateFrom, dateTo, type, 
            amountFrom, amountTo, searchQuery, 
            "date", "desc"
        );

        return exportTransactionsInFormat(transactions, format);
    }

    private void validateAccountOwnership(User user, Integer accountId) {
        Account account = accountService.getAccountById(accountId);
        if (!account.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Konto nie należy do użytkownika");
        }
    }

    private List<Transaction> fetchAndFilterTransactions(
        Integer accountId, 
        LocalDateTime dateFrom, 
        LocalDateTime dateTo, 
        String type,
        BigDecimal amountFrom, 
        BigDecimal amountTo, 
        String searchQuery,
        String sortBy,
        String sortDirection
    ) {
        List<Transaction> transactions = transactionService.getRecentTransactions(accountId, 100);
        List<Transaction> filteredTransactions = filterService.filterTransactions(
            transactions, dateFrom, dateTo, type, 
            amountFrom, amountTo, searchQuery
        );
        filterService.sortTransactions(filteredTransactions, sortBy, sortDirection);
        
        return filteredTransactions;
    }

    private ResponseEntity<Page<Transaction>> createPaginatedResponse(
        List<Transaction> filteredTransactions, 
        int page, 
        int size
    ) {
        int start = page * size;
        int end = Math.min(start + size, filteredTransactions.size());
        
        if (start > filteredTransactions.size()) {
            log.debug("Requested page {} is beyond available data", page);
            return ResponseEntity.ok(new PageImpl<>(
                List.of(), 
                PageRequest.of(page, size), 
                filteredTransactions.size()
            ));
        }

        List<Transaction> pageContent = filteredTransactions.subList(start, end);
        log.debug("Returning page {} with {} transactions", page, pageContent.size());

        return ResponseEntity.ok(new PageImpl<>(
            pageContent, 
            PageRequest.of(page, size), 
            filteredTransactions.size()
        ));
    }

    private ResponseEntity<byte[]> exportTransactionsInFormat(
        List<Transaction> filteredTransactions, 
        String format
    ) {
        TransactionExporter exporter = exporters.stream()
            .filter(e -> e.getFormat().equalsIgnoreCase(format))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(
                "Nieobsługiwany format eksportu: " + format
            ));

        return exporter.exportTransactions(filteredTransactions);
    }
}