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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryRestController {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_RECENT_TRANSACTIONS_LIMIT = 100;
    private static final Set<String> VALID_SORT_FIELDS = Set.of("date", "amount", "type");
    private static final Set<String> VALID_SORT_DIRECTIONS = Set.of("asc", "desc");

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
        validateSortParameters(sortBy, sortDirection);
        
        Account account = verifyAccountOwnership(accountId, user);
        List<Transaction> transactions = fetchAndFilterTransactions(account.getId(), dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
        return createPaginatedResponse(transactions, page, size, sortBy, sortDirection);
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
        log.debug("Exporting transactions for account {} (user: {}) in {} format", accountId, user.getUsername(), format);
        
        Account account = verifyAccountOwnership(accountId, user);
        List<Transaction> transactions = fetchAndFilterTransactions(account.getId(), dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
        return exportTransactionsToFormat(transactions, format);
    }

    private Account verifyAccountOwnership(Integer accountId, User user) {
        Account account = accountService.getAccountById(accountId);
        if (!account.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Account doesn't belong to user");
        }
        return account;
    }

    private List<Transaction> fetchAndFilterTransactions(Integer accountId,
            LocalDateTime dateFrom, LocalDateTime dateTo, String type,
            BigDecimal amountFrom, BigDecimal amountTo, String searchQuery) {
        List<Transaction> transactions = transactionService.getRecentTransactions(accountId, DEFAULT_RECENT_TRANSACTIONS_LIMIT);
        return filterService.filterTransactions(transactions, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
    }

    private void validateSortParameters(String sortBy, String sortDirection) {
        if (!VALID_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }
        if (!VALID_SORT_DIRECTIONS.contains(sortDirection.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sort direction: " + sortDirection);
        }
    }

    private ResponseEntity<Page<Transaction>> createPaginatedResponse(
            List<Transaction> transactions, int page, int size, String sortBy, String sortDirection) {
        filterService.sortTransactions(transactions, sortBy, sortDirection);

        int start = page * size;
        if (start > transactions.size()) {
            log.debug("Requested page {} is beyond available data", page);
            return ResponseEntity.ok(new PageImpl<>(List.of(), PageRequest.of(page, size), transactions.size()));
        }

        int end = Math.min(start + size, transactions.size());
        List<Transaction> pageContent = transactions.subList(start, end);
        log.debug("Returning page {} with {} transactions", page, pageContent.size());

        return ResponseEntity.ok(new PageImpl<>(pageContent, PageRequest.of(page, size), transactions.size()));
    }

    private ResponseEntity<byte[]> exportTransactionsToFormat(List<Transaction> transactions, String format) {
        TransactionExporter exporter = exporters.stream()
                .filter(e -> e.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported export format: " + format));

        return exporter.exportTransactions(transactions);
    }
}