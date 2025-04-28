package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TransactionQueryService.
 * Tests focus on business logic, error handling, and interaction with dependencies.
 */
@Slf4j
class TransactionQueryServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionQueryService queryService;

    private User createTestUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setPesel(new Pesel("12345678901"));
        user.setFirstname("Jan");
        user.setLastname("Testowy");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setUsername("testuser");
        user.setEmail(new EmailAddress("test@example.com"));
        user.setPhoneNumber(new PhoneNumber("123456789"));
        return user;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactionById_WhenTransactionExists_ShouldReturnTransaction() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // when
        Transaction result = queryService.getTransactionById(transactionId);

        // then
        assertNotNull(result);
        verify(repository).findById(transactionId);
    }

    @Test
    void getTransactionById_WhenTransactionDoesNotExist_ShouldThrowException() {
        // given
        int transactionId = 1;
        when(repository.findById(transactionId)).thenReturn(Optional.empty());

        // when/then
        assertThrows(TransactionNotFoundException.class, 
            () -> queryService.getTransactionById(transactionId));
        verify(repository).findById(transactionId);
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // given
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(repository.findAll()).thenReturn(transactions);

        // when
        List<Transaction> result = queryService.getAllTransactions();

        // then
        assertEquals(transactions.size(), result.size());
        verify(repository).findAll();
    }

    @Test
    void getAllNewTransactions_ShouldReturnOnlyNewTransactions() {
        // given
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(repository.findByStatus(TransactionStatus.NEW)).thenReturn(transactions);

        // when
        List<Transaction> result = queryService.getAllNewTransactions();

        // then
        assertEquals(transactions.size(), result.size());
        verify(repository).findByStatus(TransactionStatus.NEW);
    }

    @Test
    void getTransactionsByAccountId_WhenAccountExists_ShouldReturnTransactions() {
        // given
        int accountId = 1;
        User owner = createTestUser(1);
        Account account = Account.factory().createAccount(owner);
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        
        when(accountService.getAccountById(accountId)).thenReturn(account);
        when(repository.findByAccountId(accountId)).thenReturn(Optional.of(transactions));

        // when
        List<Transaction> result = queryService.getTransactionsByAccountId(accountId);

        // then
        assertEquals(transactions.size(), result.size());
        verify(accountService).getAccountById(accountId);
        verify(repository).findByAccountId(accountId);
    }

    @Test
    void getTransactionsByAccountId_WhenNoTransactions_ShouldThrowException() {
        // given
        int accountId = 1;
        User owner = createTestUser(2);
        Account account = Account.factory().createAccount(owner);
        
        when(accountService.getAccountById(accountId)).thenReturn(account);
        when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

        // when/then
        assertThrows(NoTransactionsForAccountException.class, 
            () -> queryService.getTransactionsByAccountId(accountId));
        verify(accountService).getAccountById(accountId);
        verify(repository).findByAccountId(accountId);
    }

    @Test
    void getRecentTransactions_WhenTransactionsExist_ShouldReturnLimitedTransactions() {
        // given
        int accountId = 1;
        int count = 5;
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        
        when(repository.findTopNByAccountIdOrderByCreatedDesc(accountId, count))
            .thenReturn(Optional.of(transactions));

        // when
        List<Transaction> result = queryService.getRecentTransactions(accountId, count);

        // then
        assertEquals(transactions.size(), result.size());
        verify(repository).findTopNByAccountIdOrderByCreatedDesc(accountId, count);
    }

    @Test
    void getRecentTransactions_WhenNoTransactions_ShouldThrowException() {
        // given
        int accountId = 1;
        int count = 5;
        
        when(repository.findTopNByAccountIdOrderByCreatedDesc(accountId, count))
            .thenReturn(Optional.empty());

        // when/then
        assertThrows(NoTransactionsForAccountException.class, 
            () -> queryService.getRecentTransactions(accountId, count));
        verify(repository).findTopNByAccountIdOrderByCreatedDesc(accountId, count);
    }
}