package info.mackiewicz.bankapp.system.banking.history.service;

import info.mackiewicz.bankapp.presentation.exception.TransactionFilterException;
import info.mackiewicz.bankapp.presentation.exception.UnsupportedExporterException;
import info.mackiewicz.bankapp.system.banking.history.dto.TransactionFilterRequest;
import info.mackiewicz.bankapp.system.banking.history.export.TransactionExporter;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.transaction.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private static final int MAX_RECENT_TRANSACTIONS = 100;
    private static final String DEFAULT_SORT_FIELD = "date";
    private static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.DESCENDING;

    private final TransactionService transactionService;
    private final TransactionFilterService filterService;
    private final List<TransactionExporter> exporters;

    /**
     * Retrieves a paginated list of transactions for a given user and account.
     *
     * @param filter the filter criteria for transactions
     *
     * @return a paginated list of transactions
     * @throws TransactionFilterException        if the filter criteria are invalid
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
    public Page<TransactionResponse> getTransactionHistory(TransactionFilterRequest filter) {
        List<Transaction> transactions = getFilteredAndSortedTransactions(filter);
        List<TransactionResponse> responses = transactions.stream()
                .map(t -> new TransactionResponse(
                        t.getSourceAccount(),
                        t.getDestinationAccount(),
                        t))
                .toList();
        return createPaginatedResponse(responses, filter.getPage(), filter.getSize());
    }

    /**
     * Exports transactions for a given user and account in the specified format.
     *
     * @param filter the filter criteria for transactions
     * @param format the export format (e.g., CSV, PDF)
     *
     * @return a ResponseEntity containing the exported transactions as a byte array
     * @throws TransactionFilterException        if the filter criteria are invalid
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     * @throws UnsupportedExporterException      if the export format is not supported
     */
    public ResponseEntity<byte[]> exportTransactions(TransactionFilterRequest filter, String format) {
        filter.setSortBy(DEFAULT_SORT_FIELD);
        filter.setSortDirection(DEFAULT_SORT_DIRECTION);
        List<Transaction> transactions = getFilteredAndSortedTransactions(filter);
        return findExporter(format).exportTransactions(transactions);
    }

    /**
     * Retrieves and filters transactions based on the provided filter criteria.
     *
     * @param filter the filter criteria for transactions
     *
     * @return a list of filtered and sorted transactions
     * @throws TransactionFilterException        if the filter criteria are invalid
     * @throws NoTransactionsForAccountException if no transactions are found for the account
     */
    private List<Transaction> getFilteredAndSortedTransactions(TransactionFilterRequest filter) {
        List<Transaction> transactions = transactionService.getRecentTransactions(filter.getAccountId(), MAX_RECENT_TRANSACTIONS);
        List<Transaction> filteredTransactions = filterService.filterTransactions(
                transactions, filter.getDateFrom(), filter.getDateTo(),
                filter.getType(), filter.getAmountFrom(), filter.getAmountTo(),
                filter.getQuery());
        filterService.sortTransactions(filteredTransactions, filter.getSortBy(), filter.getSortDirection());
        return filteredTransactions;
    }

    private Page<TransactionResponse> createPaginatedResponse(List<TransactionResponse> transactions, int page, int size) {
        int start = page * size;
        int totalSize = transactions.size();

        if (start >= totalSize) {
            log.debug("Requested page {} is beyond available data", page);
            return new PageImpl<>(List.of(), PageRequest.of(page, size), totalSize);
        }

        int end = Math.min(start + size, totalSize);
        List<TransactionResponse> pageContent = transactions.subList(start, end);
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