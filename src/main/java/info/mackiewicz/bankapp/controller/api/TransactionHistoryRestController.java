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
    public ResponseEntity<Page<Transaction>> listTransactions(
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
        log.debug("Pobieranie transakcji dla konta {} (użytkownik: {})", accountId, user.getUsername());
        List<Transaction> filteredTransactions = getFilteredTransactions(user, accountId, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
        filterService.sortTransactions(filteredTransactions, sortBy, sortDirection);
        return paginateTransactions(filteredTransactions, page, size);
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
        log.debug("Eksport transakcji dla konta {} (użytkownik: {}) w formacie {}", accountId, user.getUsername(), format);
        List<Transaction> filteredTransactions = getFilteredTransactions(user, accountId, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
        TransactionExporter exporter = exporters.stream()
                .filter(e -> e.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Nieobsługiwany format eksportu: " + format));
        return exporter.exportTransactions(filteredTransactions);
    }

    private List<Transaction> getFilteredTransactions(User user, Integer accountId, LocalDateTime dateFrom, LocalDateTime dateTo,
                                                        String type, BigDecimal amountFrom, BigDecimal amountTo, String searchQuery) {
        Account account = accountService.getAccountById(accountId);
        validateUserOwnership(account, user);
        List<Transaction> transactions = transactionService.getRecentTransactions(accountId, Integer.MAX_VALUE);
        return filterService.filterTransactions(transactions, dateFrom, dateTo, type, amountFrom, amountTo, searchQuery);
    }

    private void validateUserOwnership(Account account, User user) {
        if (!account.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Konto nie należy do użytkownika");
        }
    }

    private ResponseEntity<Page<Transaction>> paginateTransactions(List<Transaction> transactions, int page, int size) {
        int start = Math.min(page * size, transactions.size());
        int end = Math.min(start + size, transactions.size());
        List<Transaction> pagedList = transactions.subList(start, end);
        Page<Transaction> pagedTransactions = new PageImpl<>(pagedList, PageRequest.of(page, size), transactions.size());
        return ResponseEntity.ok(pagedTransactions);
    }
}