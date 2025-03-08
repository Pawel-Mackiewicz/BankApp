package info.mackiewicz.bankapp.transaction.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.mackiewicz.bankapp.shared.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for TransactionProcessingService.
 * Tests focus on transaction processing logic and status handling.
 */
@Slf4j
class TransactionProcessingServiceTest {

    @Mock
    private TransactionProcessor processor;

    @Mock
    private TransactionValidator validator;

    @Mock
    private TransactionQueryService queryService;

    @InjectMocks
    private TransactionProcessingService processingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processTransactionById_WhenNewTransaction_ShouldProcess() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.NEW);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when
        processingService.processTransactionById(transactionId);

        // then
        verify(validator).validate(transaction);
        verify(processor).processTransaction(transaction);
    }

    @Test
    void processTransactionById_WhenDoneTransaction_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.DONE);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when/then
        assertThrows(TransactionAlreadyProcessedException.class, 
            () -> processingService.processTransactionById(transactionId));
        verify(validator).validate(transaction);
        verify(processor, never()).processTransaction(any());
    }

    @Test
    void processTransactionById_WhenFaultyTransaction_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.FAULTY);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when/then
        assertThrows(TransactionCannotBeProcessedException.class, 
            () -> processingService.processTransactionById(transactionId));
        verify(validator).validate(transaction);
        verify(processor, never()).processTransaction(any());
    }

    @Test
    void processTransactionById_WhenPendingTransaction_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when/then
        assertThrows(UnsupportedOperationException.class, 
            () -> processingService.processTransactionById(transactionId));
        verify(validator).validate(transaction);
        verify(processor, never()).processTransaction(any());
    }

    @Test
    void processTransactionById_WhenValidationFails_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.NEW);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);
        doThrow(IllegalArgumentException.class).when(validator).validate(transaction);

        // when/then
        assertThrows(IllegalArgumentException.class, 
            () -> processingService.processTransactionById(transactionId));
        verify(processor, never()).processTransaction(any());
    }

    @Test
    void processAllNewTransactions_ShouldProcessAllTransactions() {
        // given
        List<Transaction> transactions = List.of(
            createTransaction(TransactionStatus.NEW),
            createTransaction(TransactionStatus.NEW)
        );
        when(queryService.getAllNewTransactions()).thenReturn(transactions);

        // when
        processingService.processAllNewTransactions();

        // then
        verify(validator, times(2)).validate(any());
        verify(processor, times(2)).processTransaction(any());
    }

    @Test
    void processAllNewTransactions_WhenSomeTransactionsFail_ShouldContinueProcessing() {
        // given
        Transaction transaction1 = createTransaction(TransactionStatus.NEW);
        Transaction transaction2 = createTransaction(TransactionStatus.NEW);
        List<Transaction> transactions = List.of(transaction1, transaction2);
        
        when(queryService.getAllNewTransactions()).thenReturn(transactions);
        doThrow(IllegalArgumentException.class).when(validator).validate(transaction1);

        // when
        processingService.processAllNewTransactions();

        // then
        verify(validator, times(2)).validate(any());
        verify(processor, never()).processTransaction(transaction1);
        verify(processor).processTransaction(transaction2);
    }

    private Transaction createTransaction(TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setStatus(status);
        return transaction;
    }
}