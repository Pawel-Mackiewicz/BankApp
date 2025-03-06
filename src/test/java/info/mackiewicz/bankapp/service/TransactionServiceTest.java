package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.service.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.mackiewicz.bankapp.shared.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.shared.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.TransactionProcessor;
import info.mackiewicz.bankapp.transaction.service.TransactionService;

class TransactionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProcessor processor;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionById() {
        logger.info("testGetTransactionById: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, transactionId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set transaction id using reflection: " + e.getMessage());
        }

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(transactionId);

        assertEquals(transactionId, result.getId());
        logger.info("testGetTransactionById: Test passed");
    }

    @Test
    void testGetTransactionByIdNotFound() {
        logger.info("testGetTransactionByIdNotFound: Starting test");
        Integer transactionId = 1;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionById(transactionId));
        logger.info("testGetTransactionByIdNotFound: Test passed");
    }

    @Test
    void testGetAllTransactions() {
        logger.info("testGetAllTransactions: Starting test");
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
        logger.info("testGetAllTransactions: Test passed");
    }

    @Test
    void testSaveTransaction() {
        logger.info("testSaveTransaction: Starting test");
        
        // Create test accounts
        Account sourceAccount = TestAccountBuilder.createTestAccountWithId(1);
        Account destinationAccount = TestAccountBuilder.createTestAccountWithId(2);
        
        // Create and set up transaction
        Transaction transaction = new Transaction();
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set transaction id using reflection: " + e.getMessage());
        }
        
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setStatus(TransactionStatus.NEW);

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transaction);

        assertEquals(transaction.getId(), result.getId());
        assertEquals(sourceAccount, result.getSourceAccount());
        assertEquals(destinationAccount, result.getDestinationAccount());
        logger.info("testSaveTransaction: Test passed");
    }

    @Test
    void testDeleteTransaction() {
        logger.info("testDeleteTransaction: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, transactionId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set transaction id using reflection: " + e.getMessage());
        }

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransactionById(transactionId);
        logger.info("testDeleteTransaction: Test passed");
    }

    @Test
    void testGetAllNewTransactions() {
        logger.info("testGetAllNewTransactions: Starting test");
        Transaction transaction1 = new Transaction();
        transaction1.setStatus(TransactionStatus.NEW);
        Transaction transaction2 = new Transaction();
        transaction2.setStatus(TransactionStatus.NEW);
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findByStatus(TransactionStatus.NEW)).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllNewTransactions();

        assertEquals(2, result.size());
        result.forEach(transaction -> assertEquals(TransactionStatus.NEW, transaction.getStatus()));
        logger.info("testGetAllNewTransactions: Test passed");
    }

    @Test
    void testGetTransactionsByAccountId() {
        logger.info("testGetTransactionsByAccountId: Starting test");
        Integer accountId = 1;
        Account account = TestAccountBuilder.createTestAccountWithId(accountId);
        Transaction transaction1 = new Transaction();
        transaction1.setSourceAccount(account);
        Transaction transaction2 = new Transaction();
        transaction2.setDestinationAccount(account);
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(accountService.getAccountById(accountId)).thenReturn(account);
        when(transactionRepository.findByAccountId(accountId)).thenReturn(Optional.of(transactions));

        List<Transaction> result = transactionService.getTransactionsByAccountId(accountId);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(transaction ->
                (transaction.getSourceAccount() != null && transaction.getSourceAccount().getId().equals(accountId)) ||
                        (transaction.getDestinationAccount() != null && transaction.getDestinationAccount().getId().equals(accountId))));
        logger.info("testGetTransactionsByAccountId: Test passed");
    }

    @Test
    void testGetTransactionsByAccountIdNotFound() {
        logger.info("testGetTransactionsByAccountIdNotFound: Starting test");
        Integer accountId = 1;
        Account account = TestAccountBuilder.createTestAccountWithId(accountId);

        when(accountService.getAccountById(accountId)).thenReturn(account);
        when(transactionRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(NoTransactionsForAccountException.class, () -> transactionService.getTransactionsByAccountId(accountId));
        logger.info("testGetTransactionsByAccountIdNotFound: Test passed");
    }

    @Test
    void testGetRecentTransactions() {
        logger.info("testGetRecentTransactions: Starting test");
        Integer accountId = 1;
        int count = 2;
        Account account = TestAccountBuilder.createTestAccountWithId(accountId);
        Transaction transaction1 = new Transaction();
        transaction1.setSourceAccount(account);
        Transaction transaction2 = new Transaction();
        transaction2.setDestinationAccount(account);
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findTopNByAccountIdOrderByCreatedDesc(accountId, count)).thenReturn(Optional.of(transactions));

        List<Transaction> result = transactionService.getRecentTransactions(accountId, count);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(transaction ->
                (transaction.getSourceAccount() != null && transaction.getSourceAccount().getId().equals(accountId)) ||
                        (transaction.getDestinationAccount() != null && transaction.getDestinationAccount().getId().equals(accountId))));
        logger.info("testGetRecentTransactions: Test passed");
    }

    @Test
    void testGetRecentTransactionsNotFound() {
        logger.info("testGetRecentTransactionsNotFound: Starting test");
        Integer accountId = 1;
        int count = 2;

        when(transactionRepository.findTopNByAccountIdOrderByCreatedDesc(accountId, count)).thenReturn(Optional.empty());

        assertThrows(NoTransactionsForAccountException.class, () -> transactionService.getRecentTransactions(accountId, count));
        logger.info("testGetRecentTransactionsNotFound: Test passed");
    }

    @Test
    void testProcessTransactionById() {
        logger.info("testProcessTransactionById: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, transactionId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set transaction id using reflection: " + e.getMessage());
        }
        transaction.setStatus(TransactionStatus.NEW);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        doNothing().when(processor).processTransaction(transaction);

        transactionService.processTransactionById(transactionId);

        verify(processor, times(1)).processTransaction(transaction);
        logger.info("testProcessTransactionById: Test passed");
    }

    @Test
    void testProcessTransactionByIdAlreadyDone() {
        logger.info("testProcessTransactionByIdAlreadyDone: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, transactionId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set transaction id using reflection: " + e.getMessage());
        }
        transaction.setStatus(TransactionStatus.DONE);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThrows(TransactionAlreadyProcessedException.class, () -> transactionService.processTransactionById(transactionId));
        logger.info("testProcessTransactionByIdAlreadyDone: Test passed");
    }

    @Test
    void testProcessTransactionByIdFaulty() {
        logger.info("testProcessTransactionByIdFaulty: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        try {
            Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, transactionId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set transaction id using reflection: " + e.getMessage());
        }
        transaction.setStatus(TransactionStatus.FAULTY);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        assertThrows(TransactionCannotBeProcessedException.class, () -> transactionService.processTransactionById(transactionId));
        logger.info("testProcessTransactionByIdFaulty: Test passed");
    }

    @Test
    void testProcessAllNewTransactions() {
        logger.info("testProcessAllNewTransactions: Starting test");
        Transaction transaction1 = new Transaction();
        transaction1.setStatus(TransactionStatus.NEW);
        Transaction transaction2 = new Transaction();
        transaction2.setStatus(TransactionStatus.NEW);
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findByStatus(TransactionStatus.NEW)).thenReturn(transactions);
        doNothing().when(processor).processTransaction(any(Transaction.class));

        transactionService.processAllNewTransactions();

        verify(processor, times(2)).processTransaction(any(Transaction.class));
        logger.info("testProcessAllNewTransactions: Test passed");
    }

    @Disabled
    @Test
    void testDisabled() {
        logger.info("testDisabled: Starting test");
        assertTrue(false);
        logger.info("testDisabled: Test passed");
    }
}