package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionFilterService {

    public List<Transaction> filterTransactions(List<Transaction> transactions,
                                               LocalDateTime dateFrom,
                                               LocalDateTime dateTo,
                                               String type,
                                               BigDecimal amountFrom,
                                               BigDecimal amountTo,
                                               String searchQuery) {
        return transactions.stream()
                .filter(t -> dateFrom == null || !t.getDate().isBefore(dateFrom))
                .filter(t -> dateTo == null || !t.getDate().isAfter(dateTo))
                .filter(t -> type == null || filterByType(t, type))
                .filter(t -> amountFrom == null || t.getAmount().compareTo(amountFrom) >= 0)
                .filter(t -> amountTo == null || t.getAmount().compareTo(amountTo) <= 0)
                .filter(t -> matches(t, searchQuery))
                .collect(Collectors.toList());
    }

    private boolean filterByType(Transaction transaction, String type) {
        if (type.equals("TRANSFER")) {
            return transaction.getType().getCategory().toString().equals("TRANSFER");
        }
        return transaction.getType().toString().equals(type);
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

    public void sortTransactions(List<Transaction> transactions, String sortBy, String sortDirection) {
        transactions.sort((t1, t2) -> {
            int multiplier = sortDirection.equalsIgnoreCase("asc") ? 1 : -1;
            return switch (sortBy.toLowerCase()) {
                case "date" -> multiplier * t1.getDate().compareTo(t2.getDate());
                case "amount" -> multiplier * t1.getAmount().compareTo(t2.getAmount());
                case "type" -> multiplier * t1.getType().toString().compareTo(t2.getType().toString());
                default -> multiplier * t1.getDate().compareTo(t2.getDate());
            };
        });
    }
}