package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionFilterService;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransactionFilterDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.export.TransactionExporter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryRestController {

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
            @Valid TransactionFilterDTO filter
    ) {
        log.debug("Fetching transactions for account {} (user: {})", filter.getAccountId(), user.getUsername());
        
        verifyAccountOwnership(user, filter.getAccountId());
        
        List<Transaction> transactions = getFilteredAndSortedTransactions(filter);
        
        return ResponseEntity.ok(createPaginatedResponse(transactions, filter.getPage(), filter.getSize()));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal User user,
            @Valid TransactionFilterDTO filter,
            @RequestParam(defaultValue = DEFAULT_EXPORT_FORMAT) String format
    ) {
        log.debug("Exporting transactions for account {} (user: {}) in {} format",
                filter.getAccountId(), user.getUsername(), format);

        verifyAccountOwnership(user, filter.getAccountId());
        
        // Set default sorting for export
        filter.setSortBy(DEFAULT_SORT_FIELD);
        filter.setSortDirection(DEFAULT_SORT_DIRECTION);
        
        List<Transaction> transactions = getFilteredAndSortedTransactions(filter);

        TransactionExporter exporter = findExporter(format);
        return exporter.exportTransactions(transactions);
    }

    private void verifyAccountOwnership(User user, Integer accountId) {
        Account account = accountService.getAccountById(accountId);
        if (!account.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Account doesn't belong to user");
        }
    }

    private List<Transaction> getFilteredAndSortedTransactions(TransactionFilterDTO filter) {
        List<Transaction> transactions = transactionService.getRecentTransactions(filter.getAccountId(), MAX_RECENT_TRANSACTIONS);
        List<Transaction> filteredTransactions = filterService.filterTransactions(
                transactions, filter.getDateFrom(), filter.getDateTo(),
                filter.getType(), filter.getAmountFrom(), filter.getAmountTo(),
                filter.getQuery());
        filterService.sortTransactions(filteredTransactions, filter.getSortBy(), filter.getSortDirection());
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