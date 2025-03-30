package info.mackiewicz.bankapp.presentation.dashboard.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.presentation.exception.TransactionFilterException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;

import org.hibernate.query.SortDirection;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionFilterService {

    /**
     * Filters a list of transactions based on various criteria.
     *
     * @param transactions the list of transactions to filter
     * @param dateFrom     the start date for filtering (inclusive)
     * @param dateTo       the end date for filtering (inclusive)
     * @param type         the type of transaction to filter by (e.g., "TRANSFER_OWN", "TRANSFER_INTERNAL", "DEPOSIT", "WITHDRAWAL", "FEE")
     * @param amountFrom   the minimum amount for filtering (inclusive)
     * @param amountTo     the maximum amount for filtering (inclusive)
     * @param searchQuery  a search query to match against transaction titles and account details
     * @return a filtered list of transactions
     * @throws TransactionFilterException if an unexpected error occurs during filtering
     */
    public List<Transaction> filterTransactions(List<Transaction> transactions,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            TransactionType type,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String searchQuery) {
        try {
            return transactions.stream()
                    .filter(t -> dateFrom == null || !t.getDate().isBefore(dateFrom))
                    .filter(t -> dateTo == null || !t.getDate().isAfter(dateTo))
                    .filter(t -> type == null || t.getType().equals(type))
                    .filter(t -> amountFrom == null || t.getAmount().compareTo(amountFrom) >= 0)
                    .filter(t -> amountTo == null || t.getAmount().compareTo(amountTo) <= 0)
                    .filter(t -> matches(t, searchQuery))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TransactionFilterException("Unexpected error while filtering transactions", e);
        }
    }

    private boolean matches(Transaction transaction, String searchQuery) {
        if (searchQuery == null) {
            return true;
        }

        String query = searchQuery.toLowerCase();
        return transaction.getTitle().toLowerCase().contains(query) ||
                matchesAccount(transaction.getSourceAccount(), query) ||
                matchesAccount(transaction.getDestinationAccount(), query);
    }

    private boolean matchesAccount(Account account, String query) {
        if (account == null) {
            return false;
        }

        return Optional.ofNullable(account.getId())
                .map(String::valueOf)
                .map(id -> id.contains(query))
                .orElse(false) ||
                Optional.ofNullable(account.getOwner().getFullName())
                        .map(owner -> {
                            String fullName = owner.toLowerCase();
                            return fullName.contains(query);
                        })
                        .orElse(false);
    }

    /**
     * Sorts a list of transactions based on the specified criteria.
     *
     * @param transactions  the list of transactions to sort
     * @param sortBy       the field to sort by (e.g., "date", "amount", "type")
     * @param sortDirection the direction to sort (e.g., "asc" or "desc")
     * @throws TransactionFilterException if an unexpected error occurs during sorting
     */
    public void sortTransactions(List<Transaction> transactions, String sortBy, SortDirection sortDirection) {
        try {
            transactions.sort((t1, t2) -> {
                int multiplier = sortDirection.equals(SortDirection.ASCENDING) ? 1 : -1;    
                return switch (sortBy.toLowerCase()) {
                    case "date" -> multiplier * t1.getDate().compareTo(t2.getDate());
                    case "amount" -> multiplier * t1.getAmount().compareTo(t2.getAmount());
                    case "type" -> multiplier * t1.getType().toString().compareTo(t2.getType().toString());
                    default -> multiplier * t1.getDate().compareTo(t2.getDate());
                };
            });
        } catch (Exception e) {
            throw new TransactionFilterException("Unexpected error while sorting transactions", e);
        }
    }
}