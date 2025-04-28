package info.mackiewicz.bankapp.system.banking.history.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.user.model.User;
import org.hibernate.query.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFilterServiceTest {

    private TransactionFilterService transactionFilterService;

    @BeforeEach
    void setUp() {
        transactionFilterService = new TransactionFilterService();
    }

    @Test
    void filterTransactions_NoFilters_ReturnsAllTransactions() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, null, null);

        // Assert
        assertEquals(transactions.size(), filteredTransactions.size());
    }

    @Test
    void filterTransactions_FilterByDateFrom_ReturnsTransactionsAfterDateFrom() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 15, 12, 0);

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, dateFrom, null, null, null, null, null, null);

        // Assert
        assertEquals(2, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().noneMatch(t -> t.getDate().isBefore(dateFrom)));
    }

    @Test
    void filterTransactions_FilterByDateTo_ReturnsTransactionsBeforeDateTo() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        LocalDateTime dateTo = LocalDateTime.of(2024, 1, 15, 12, 0);

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, dateTo, null, null, null, null, null);

        // Assert
        assertEquals(2, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().noneMatch(t -> t.getDate().isAfter(dateTo)));
    }

    @Test
    void filterTransactions_FilterByDateFromAndDateTo_ReturnsTransactionsBetweenDates() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        LocalDateTime dateFrom = LocalDateTime.of(2024, 1, 10, 0, 0);
        LocalDateTime dateTo = LocalDateTime.of(2024, 1, 20, 23, 59);

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, dateFrom, dateTo, null, null, null, null, null);

        // Assert
        assertEquals(2, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().allMatch(t -> !t.getDate().isBefore(dateFrom) && !t.getDate().isAfter(dateTo)));
    }

    @Test
    void filterTransactions_FilterByTypeTransfer_ReturnsTransferTransactions() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        TransactionType type = TransactionType.TRANSFER_INTERNAL;

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, type, null, null, null, null);

        // Assert
        assertEquals(1, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().allMatch(t -> t.getType().equals(type)));
    }

    @Test
    void filterTransactions_FilterByTypeDeposit_ReturnsDepositTransactions() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        TransactionType type = TransactionType.DEPOSIT;

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, type, null, null, null, null);

        // Assert
        assertEquals(1, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().allMatch(t -> t.getType().equals(type)));
    }

    @Test
    void filterTransactions_FilterByAmountFrom_ReturnsTransactionsWithAmountGreaterOrEqualTo() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        BigDecimal amountFrom = BigDecimal.valueOf(500);

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, amountFrom, null, null);

        // Assert
        assertEquals(2, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().allMatch(t -> t.getAmount().compareTo(amountFrom) >= 0));
    }

    @Test
    void filterTransactions_FilterByAmountTo_ReturnsTransactionsWithAmountLessOrEqualTo() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        BigDecimal amountTo = BigDecimal.valueOf(1000);

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, amountTo, null);

        // Assert
        assertEquals(3, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().allMatch(t -> t.getAmount().compareTo(amountTo) <= 0));
    }

    @Test
    void filterTransactions_FilterByAmountFromAndAmountTo_ReturnsTransactionsWithinAmountRange() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        BigDecimal amountFrom = BigDecimal.valueOf(500);
        BigDecimal amountTo = BigDecimal.valueOf(1500);

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, amountFrom, amountTo, null);

        // Assert
        assertEquals(1, filteredTransactions.size());
        assertTrue(filteredTransactions.stream().allMatch(t -> t.getAmount().compareTo(amountFrom) >= 0 && t.getAmount().compareTo(amountTo) <= 0));
    }

    @Test
    void filterTransactions_FilterBySearchQueryNull_ReturnsAllTransactions() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, null, null);

        // Assert
        assertEquals(transactions.size(), filteredTransactions.size());
    }

    @Test
    void filterTransactions_FilterBySearchQueryEmpty_ReturnsAllTransactions() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, null, "");

        // Assert
        assertEquals(transactions.size(), filteredTransactions.size());
    }

    @Test
    void filterTransactions_FilterBySearchQueryInTitle_ReturnsMatchingTransaction() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        String searchQuery = "transfer";

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, null, searchQuery);

        // Assert
        assertEquals(1, filteredTransactions.size());
        assertTrue(filteredTransactions.getFirst().getTitle().toLowerCase().contains(searchQuery));
    }

    @Test
    void filterTransactions_FilterBySearchQueryInAccountId_ReturnsMatchingTransaction() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        String searchQuery = "1";

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, null, searchQuery);

        // Assert
        assertTrue(filteredTransactions.stream().anyMatch(t ->
                (t.getSourceAccount() != null && t.getSourceAccount().getId().toString().contains(searchQuery)) ||
                        (t.getDestinationAccount() != null && t.getDestinationAccount().getId().toString().contains(searchQuery))
        ));
    }

    @Test
    void filterTransactions_FilterBySearchQueryInOwnerName_ReturnsMatchingTransaction() {
        // Arrange
        List<Transaction> transactions = createTransactions();
        String searchQuery = "jan";

        // Act
        List<Transaction> filteredTransactions = transactionFilterService.filterTransactions(transactions, null, null, null, null, null, null, searchQuery);

        // Assert
        assertTrue(filteredTransactions.stream().anyMatch(t ->
                (t.getSourceAccount() != null && t.getSourceAccount().getOwner().getFullName().toLowerCase().contains(searchQuery)) ||
                        (t.getDestinationAccount() != null && t.getDestinationAccount().getOwner().getFullName().toLowerCase().contains(searchQuery))
        ));
    }

    private List<Transaction> createTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        User user = new User();
        user.setId(2);
        user.setFirstname("Jan");
        user.setLastname("Kowalski");
        Account account1 = TestAccountBuilder.createTestAccountWithOwner(user);
        TestAccountBuilder.setField(account1, "id", 1);

        Account account2 = TestAccountBuilder.createTestAccountWithOwner(user);
        TestAccountBuilder.setField(account2, "id", 2);

        Transaction transaction1 = new Transaction();
        transaction1.setDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        transaction1.setType(TransactionType.DEPOSIT);
        transaction1.setAmount(BigDecimal.valueOf(100));
        transaction1.setTitle("Deposit");
        transaction1.setDestinationAccount(account1);
        transactions.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setDate(LocalDateTime.of(2024, 1, 10, 12, 0));
        transaction2.setType(TransactionType.WITHDRAWAL);
        transaction2.setAmount(BigDecimal.valueOf(1000));
        transaction2.setTitle("Withdrawal");
        transaction2.setSourceAccount(account1);
        transactions.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setDate(LocalDateTime.of(2024, 1, 15, 15, 0));
        transaction3.setType(TransactionType.TRANSFER_INTERNAL);
        transaction3.setAmount(BigDecimal.valueOf(2000));
        transaction3.setTitle("Internal Transfer");
        transaction3.setSourceAccount(account1);
        transaction3.setDestinationAccount(account2);
        transactions.add(transaction3);

        Transaction transaction4 = new Transaction();
        transaction4.setDate(LocalDateTime.of(2024, 1, 25, 9, 0));
        transaction4.setType(TransactionType.FEE);
        transaction4.setAmount(BigDecimal.valueOf(10));
        transaction4.setTitle("Fee");
        transaction4.setDestinationAccount(account1);
        transactions.add(transaction4);

        return transactions;
    }

    @Test
    void sortTransactions_SortByDateAsc_SortsByDateAscending() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "date", SortDirection.ASCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertFalse(transactions.get(i).getDate().isAfter(transactions.get(i + 1).getDate()));
        }
    }

    @Test
    void sortTransactions_SortByDateDesc_SortsByDateDescending() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "date", SortDirection.DESCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertFalse(transactions.get(i).getDate().isBefore(transactions.get(i + 1).getDate()));
        }
    }

    @Test
    void sortTransactions_SortByAmountAsc_SortsByAmountAscending() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "amount", SortDirection.ASCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertTrue(transactions.get(i).getAmount().compareTo(transactions.get(i + 1).getAmount()) <= 0);
        }
    }

    @Test
    void sortTransactions_SortByAmountDesc_SortsByAmountDescending() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "amount", SortDirection.DESCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertTrue(transactions.get(i).getAmount().compareTo(transactions.get(i + 1).getAmount()) >= 0);
        }
    }

    @Test
    void sortTransactions_SortByTypeAsc_SortsByTypeAscending() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "type", SortDirection.ASCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertTrue(transactions.get(i).getType().toString().compareTo(transactions.get(i + 1).getType().toString()) <= 0);
        }
    }

    @Test
    void sortTransactions_SortByTypeDesc_SortsByTypeDescending() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "type", SortDirection.DESCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertTrue(transactions.get(i).getType().toString().compareTo(transactions.get(i + 1).getType().toString()) >= 0);
        }
    }

    @Test
    void sortTransactions_DefaultSort_SortsByDate() {
        // Arrange
        List<Transaction> transactions = createTransactions();

        // Act
        transactionFilterService.sortTransactions(transactions, "invalid", SortDirection.ASCENDING);

        // Assert
        for (int i = 0; i < transactions.size() - 1; i++) {
            assertFalse(transactions.get(i).getDate().isAfter(transactions.get(i + 1).getDate()));
        }
    }
}