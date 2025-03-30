package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransactionFilterDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryRestController implements TransactionHistoryRestControllerInterface {

    private static final String DEFAULT_EXPORT_FORMAT = "csv";
    
    private final TransactionHistoryService transactionHistoryService;

    @Override
    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @AuthenticationPrincipal User user, 
            TransactionFilterDTO filter
    ) {
        log.debug("Fetching transactions for account {} (user: {})", filter.getAccountId(), user.getUsername());
        return ResponseEntity.ok(transactionHistoryService.getTransactionHistory(user.getId(), filter));
    }

    @Override
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactions(
            @AuthenticationPrincipal User user,
            TransactionFilterDTO filter,
            @RequestParam(defaultValue = DEFAULT_EXPORT_FORMAT) String format
    ) {
        log.debug("Exporting transactions for account {} (user: {}) in {} format",
                filter.getAccountId(), user.getUsername(), format);
        return transactionHistoryService.exportTransactions(user.getId(), filter, format);
    }
}