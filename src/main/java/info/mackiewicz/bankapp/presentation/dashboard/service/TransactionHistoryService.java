package info.mackiewicz.bankapp.presentation.dashboard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransactionFilterDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.export.TransactionExporter;
import info.mackiewicz.bankapp.presentation.exception.TransactionFilterException;
import info.mackiewicz.bankapp.presentation.exception.UnsupportedExporterException;
import info.mackiewicz.bankapp.transaction.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private static final int MAX_RECENT_TRANSACTIONS = 100;
    private static final String DEFAULT_SORT_FIELD = "date";
    private static final String DEFAULT_SORT_DIRECTION = "desc";

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final TransactionFilterService filterService;
    private final List<TransactionExporter> exporters;

    /**
     * Retrieves a paginated list of transactions for a given user and account.
     *
     * @param userId the ID of the user who owns the account
     * @param filter the filter criteria for transactions
     * @return a paginated list of transactions
     * @throws AccessDeniedException if the user does not own the account
     * @throws TransactionFilterException if the filter criteria are invalid
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
    public Page<Transaction> getTransactionHistory(Integer userId, TransactionFilterDTO filter) {
        verifyAccountOwnership(userId, filter.getAccountId());
        List<Transaction> transactions = getFilteredAndSortedTransactions(filter);
        return createPaginatedResponse(transactions, filter.getPage(), filter.getSize());
    }

    /**
     * Exports transactions for a given user and account in the specified format.
     *
     * @param userId the ID of the user
     * @param filter  the filter criteria for transactions
     * @param format  the export format (e.g., CSV, PDF)
     * @return a ResponseEntity containing the exported transactions as a byte array
     * @throws AccountNotFoundByIdException if the account does not exist
     * @throws AccessDeniedException if the user does not own the account
     * @throws TransactionFilterException if the filter criteria are invalid
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     * @throws UnsupportedOperationException if the export format is not supported
     */
    public ResponseEntity<byte[]> exportTransactions(Integer userId, TransactionFilterDTO filter, String format) {
        verifyAccountOwnership(userId, filter.getAccountId());
        filter.setSortBy(DEFAULT_SORT_FIELD);
        filter.setSortDirection(DEFAULT_SORT_DIRECTION);
        List<Transaction> transactions = getFilteredAndSortedTransactions(filter);
        return findExporter(format).exportTransactions(transactions);
    }

    /**
     * Verifies if the user owns the specified account.
     *
     * @param userId    the ID of the user
     * @param accountId the ID of the account
     * @throws AccountNotFoundByIdException if the account does not exist
     * @throws AccessDeniedException if the account does not belong to the user
     */
    private void verifyAccountOwnership(Integer userId, Integer accountId) {
        Account account = accountService.getAccountById(accountId);
        if (!account.getOwner().getId().equals(userId)) {
            throw new AccountOwnershipException("Account " + accountId + " doesn't belong to user with ID: " + userId);
        }
    }

    /**
     * Retrieves and filters transactions based on the provided filter criteria.
     *
     * @param filter the filter criteria for transactions
     * @return a list of filtered and sorted transactions
     * @throws TransactionFilterException if the filter criteria are invalid
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
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
                .orElseThrow(() -> new UnsupportedExporterException("Unsupported export format: " + format));
    }
}