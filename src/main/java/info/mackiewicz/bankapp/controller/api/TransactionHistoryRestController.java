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
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            log.debug("Fetching transactions for account {} (user: {})", accountId, user.getUsername());

            Account account = validateAccountAccess(user, accountId);

            List<Transaction> filteredTransactions = getFilteredTransactions(accountId, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery, sortBy, sortDirection, 100);

            return applyPagination(page, size, filteredTransactions);
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user {} to account {}: {}", user.getUsername(), accountId, e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Error fetching transactions for account {}: {}", accountId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
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
            @RequestParam(defaultValue = "csv") String format) {
        try {
            log.debug("Exporting transactions for account {} (user: {}) in {} format", accountId, user.getUsername(),
                    format);

            Account account = validateAccountAccess(user, accountId);

            List<Transaction> filteredTransactions = getFilteredTransactions(accountId, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery, null, null, Integer.MAX_VALUE);

            TransactionExporter exporter = exporters.stream()
                    .filter(e -> e.getFormat().equalsIgnoreCase(format))
                    .findFirst()
                    .orElseThrow(() -> new UnsupportedOperationException("Unsupported export format: " + format));

            return exporter.exportTransactions(filteredTransactions);
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user {} to account {}: {}", user.getUsername(), accountId, e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (UnsupportedOperationException e) {
            log.warn("Unsupported export format: {}", format);
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            log.error("Error exporting transactions for account {}: {}", accountId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    private Account validateAccountAccess(User user, Integer accountId) {
        Account account = accountService.getAccountById(accountId);
        if (!account.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Account doesn't belong to user");
        }
        return account;
    }

    private List<Transaction> getFilteredTransactions(Integer accountId, LocalDateTime dateFrom, LocalDateTime dateTo, String type, BigDecimal amountFrom, BigDecimal amountTo, String searchQuery, String sortBy, String sortDirection, int limit) {
        List<Transaction> transactions = transactionService.getRecentTransactions(accountId, limit);
        List<Transaction> filteredTransactions = filterService.filterTransactions(
                transactions, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
        if (sortBy != null && sortDirection != null) {
            filterService.sortTransactions(filteredTransactions, sortBy, sortDirection);
        }
        return filteredTransactions;
    }

    private ResponseEntity<Page<Transaction>> applyPagination(int page, int size, List<Transaction> filteredTransactions) {
        int start = page * size;
        int end = Math.min(start + size, filteredTransactions.size());

        if (start > filteredTransactions.size()) {
            log.debug("Requested page {} is beyond available data", page);
            return ResponseEntity
                    .ok(new PageImpl<>(List.of(), PageRequest.of(page, size), filteredTransactions.size()));
        }

        List<Transaction> pageContent = filteredTransactions.subList(start, end);
        log.debug("Returning page {} with {} transactions", page, pageContent.size());

        return ResponseEntity.ok(new PageImpl<>(pageContent, PageRequest.of(page, size), filteredTransactions.size()));
    }
}