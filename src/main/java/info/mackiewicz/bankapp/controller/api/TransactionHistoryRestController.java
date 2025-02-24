package info.mackiewicz.bankapp.controller.api;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionFilterService;
import info.mackiewicz.bankapp.service.TransactionService;
import info.mackiewicz.bankapp.service.export.TransactionExporter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryRestController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_RECENT_TRANSACTIONS = 100;
    private static final String DEFAULT_SORT_FIELD = "date";
    private static final String DEFAULT_SORT_DIRECTION = "desc";
    private static final String DEFAULT_EXPORT_FORMAT = "csv";

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final TransactionFilterService filterService;
    private final List<TransactionExporter> exporters;

    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @AuthenticationPrincipal User user,
            @NotNull @RequestParam Integer accountId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) @Min(1) int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortBy,
            @RequestParam(defaultValue = DEFAULT_SORT_DIRECTION) String sortDirection
    ) {
        log.debug("Fetching transactions for account {} (user: {})", accountId, user.getUsername());
        
        verifyAccountOwnership(user, accountId);
        
        List<Transaction> transactions = getFilteredAndSortedTransactions(
                accountId, dateFrom, dateTo, type, amountFrom, amountTo, query, sortBy, sortDirection);
        
        return ResponseEntity.ok(createPaginatedResponse(transactions, page, size));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal User user,
            @NotNull @RequestParam Integer accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = DEFAULT_EXPORT_FORMAT) String format
    ) {
        log.debug("Exporting transactions for account {} (user: {}) in {} format", accountId, user.getUsername(), format);

        verifyAccountOwnership(user, accountId);

        List<Transaction> transactions = getFilteredAndSortedTransactions(
                accountId, dateFrom, dateTo, type, amountFrom, amountTo, query, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);

        TransactionExporter exporter = findExporter(format);
        return exporter.exportTransactions(transactions);
    }

    private void verifyAccountOwnership(User user, Integer accountId) {
        Account account = accountService.getAccountById(accountId);
        if (!account.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Account doesn't belong to user");
        }
    }

    private List<Transaction> getFilteredAndSortedTransactions(
            Integer accountId,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String type,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String query,
            String sortBy,
            String sortDirection
    ) {
        List<Transaction> transactions = transactionService.getRecentTransactions(accountId, MAX_RECENT_TRANSACTIONS);
        List<Transaction> filteredTransactions = filterService.filterTransactions(
                transactions, dateFrom, dateTo, type, amountFrom, amountTo, query);
        filterService.sortTransactions(filteredTransactions, sortBy, sortDirection);
        return filteredTransactions;
    }

    private Page<Transaction> createPaginatedResponse(List<Transaction> transactions, int page, int size) {
        int start = page * size;
        int totalSize = transactions.size();

        if (start >= totalSize) {
            log.debug("Requested page {} is beyond available data", page);
            return new PageImpl<>(List.of(), PageRequest.of(page, size), totalSize);
        }

        int end = Math.min(start + size, totalSize);
        List<Transaction> pageContent = transactions.subList(start, end);
        log.debug("Returning page {} with {} transactions", page, pageContent.size());

        return new PageImpl<>(pageContent, PageRequest.of(page, size), totalSize);
    }

    private TransactionExporter findExporter(String format) {
        return exporters.stream()
                .filter(e -> e.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported export format: " + format));
    }
}