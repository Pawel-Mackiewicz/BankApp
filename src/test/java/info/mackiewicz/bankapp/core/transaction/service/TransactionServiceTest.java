package info.mackiewicz.bankapp.core.transaction.service;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TransactionService facade.
 * These tests verify that the facade properly delegates calls to specialized services.
 */
@Slf4j
class TransactionServiceTest {

    @Mock
    private TransactionQueryService queryService;

    @Mock
    private TransactionCommandService commandService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_ShouldDelegateToCommandService() {
        // given
        Transaction transaction = new Transaction();
        when(commandService.registerTransaction(transaction)).thenReturn(transaction);

        // when
        Transaction result = transactionService.registerTransaction(transaction);

        // then
        verify(commandService).registerTransaction(transaction);
        assertEquals(transaction, result);
    }

    @Test
    void deleteTransactionById_ShouldDelegateToCommandService() {
        // given
        int transactionId = 1;

        // when
        transactionService.deleteTransactionById(transactionId);

        // then
        verify(commandService).deleteTransactionById(transactionId);
    }

    @Test
    void getTransactionById_ShouldDelegateToQueryService() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when
        Transaction result = transactionService.getTransactionById(transactionId);

        // then
        verify(queryService).getTransactionById(transactionId);
        assertEquals(transaction, result);
    }

    @Test
    void getAllTransactions_ShouldDelegateToQueryService() {
        // given
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(queryService.getAllTransactions()).thenReturn(transactions);

        // when
        List<Transaction> result = transactionService.getAllTransactions();

        // then
        verify(queryService).getAllTransactions();
        assertEquals(transactions, result);
    }

    @Test
    void getAllNewTransactions_ShouldDelegateToQueryService() {
        // given
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(queryService.getAllNewTransactions()).thenReturn(transactions);

        // when
        List<Transaction> result = transactionService.getAllNewTransactions();

        // then
        verify(queryService).getAllNewTransactions();
        assertEquals(transactions, result);
    }

    @Test
    void getTransactionsByAccountId_ShouldDelegateToQueryService() {
        // given
        int accountId = 1;
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(queryService.getTransactionsByAccountId(accountId)).thenReturn(transactions);

        // when
        List<Transaction> result = transactionService.getTransactionsByAccountId(accountId);

        // then
        verify(queryService).getTransactionsByAccountId(accountId);
        assertEquals(transactions, result);
    }

    @Test
    void getRecentTransactions_ShouldDelegateToQueryService() {
        // given
        int accountId = 1;
        int count = 5;
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(queryService.getRecentTransactions(accountId, count)).thenReturn(transactions);

        // when
        List<Transaction> result = transactionService.getRecentTransactions(accountId, count);

        // then
        verify(queryService).getRecentTransactions(accountId, count);
        assertEquals(transactions, result);
    }
}