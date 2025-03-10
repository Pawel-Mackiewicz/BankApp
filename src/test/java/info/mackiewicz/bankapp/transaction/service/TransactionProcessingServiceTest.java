package info.mackiewicz.bankapp.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.service.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for TransactionProcessingService.
 * Tests focus on transaction processing logic and centralized error handling.
 */
@Slf4j
class TransactionProcessingServiceTest {

    @Mock
    private TransactionProcessor processor;

    @Mock
    private TransactionValidator validator;

    @Mock
    private TransactionQueryService queryService;

    @Mock
    private TransactionStatusChecker statusChecker;
    
    @Mock
    private TransactionErrorHandler errorHandler;

    @InjectMocks
    private TransactionProcessingService processingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processTransactionById_ShouldValidateAndProcess() {
        // given
        int transactionId = 1;
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);
        
        // when
        processingService.processTransactionById(transactionId);
        
        // then
        verify(validator).validate(transaction);
        verify(statusChecker).validateForProcessing(transaction);
        verify(processor).processTransaction(transaction);
    }

    @Test
    void processTransactionById_WhenValidationFails_ShouldHandleError() {
        // given
        int transactionId = 1;
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        TransactionValidationException exception = new TransactionValidationException("Validation failed");
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);
        doThrow(exception).when(validator).validate(transaction);
        
        // when
        processingService.processTransactionById(transactionId);
        
        // then
        verify(errorHandler).handleValidationError(transaction, exception);
        verify(processor, never()).processTransaction(any());
    }
    
    @Test
    void processTransactionById_WhenStatusCheckFails_ShouldHandleError() {
        // given
        int transactionId = 1;
        Transaction transaction = createTransaction(TransactionStatus.DONE);
        TransactionAlreadyProcessedException exception = new TransactionAlreadyProcessedException("Already processed");
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);
        doThrow(exception).when(statusChecker).validateForProcessing(transaction);
        
        // when
        processingService.processTransactionById(transactionId);
        
        // then
        verify(errorHandler).handleUnexpectedError(eq(transaction), any(Exception.class));
        verify(processor, never()).processTransaction(any());
    }

    // W obecnej implementacji błędy wykonania transakcji są obsługiwane w TransactionProcessor
    // a nie w TransactionProcessingService, więc poniższe testy są niepoprawne

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
        verify(statusChecker, times(2)).validateForProcessing(any());
        verify(processor, times(2)).processTransaction(any());
    }
    
    @Test
    void processAllNewTransactions_WhenSomeTransactionsFail_ShouldContinueProcessing() {
        // given
        Transaction transaction1 = createTransaction(TransactionStatus.NEW);
        transaction1.setId(1);
        Transaction transaction2 = createTransaction(TransactionStatus.NEW);
        transaction2.setId(2);
        List<Transaction> transactions = List.of(transaction1, transaction2);
        
        when(queryService.getAllNewTransactions()).thenReturn(transactions);
        doThrow(new TransactionValidationException("Validation error")).when(validator).validate(transaction1);
        
        // when
        processingService.processAllNewTransactions();
        
        // then
        verify(validator, times(2)).validate(any());
        verify(errorHandler).handleValidationError(eq(transaction1), any(TransactionValidationException.class));
        verify(statusChecker, times(1)).validateForProcessing(transaction2);
        verify(processor, never()).processTransaction(transaction1);
        verify(processor).processTransaction(transaction2);
    }

    @Test
    void processAllNewTransactions_WhenStatusCheckFails_ShouldContinueProcessing() {
        // given
        Transaction transaction1 = createTransaction(TransactionStatus.DONE); // Status uniemożliwia przetwarzanie
        transaction1.setId(1);
        Transaction transaction2 = createTransaction(TransactionStatus.NEW);
        transaction2.setId(2);
        List<Transaction> transactions = List.of(transaction1, transaction2);
        
        when(queryService.getAllNewTransactions()).thenReturn(transactions);
        doThrow(new TransactionAlreadyProcessedException("Already processed")).when(statusChecker)
            .validateForProcessing(transaction1);
        
        // when
        processingService.processAllNewTransactions();
        
        // then
        verify(validator, times(2)).validate(any());
        verify(statusChecker, times(2)).validateForProcessing(any());
        verify(errorHandler).handleUnexpectedError(eq(transaction1), any(Exception.class));
        verify(processor, never()).processTransaction(transaction1);
        verify(processor).processTransaction(transaction2);
    }

    @Test
    void processAllNewTransactions_WhenProcessorThrowsException_ShouldContinueProcessing() {
        // given
        Transaction transaction1 = createTransaction(TransactionStatus.NEW);
        transaction1.setId(1);
        Transaction transaction2 = createTransaction(TransactionStatus.NEW);
        transaction2.setId(2);
        List<Transaction> transactions = List.of(transaction1, transaction2);
        
        when(queryService.getAllNewTransactions()).thenReturn(transactions);
        doThrow(new RuntimeException("Processor error")).when(processor).processTransaction(transaction1);
        
        // when
        processingService.processAllNewTransactions();
        
        // then
        verify(validator, times(2)).validate(any());
        verify(statusChecker, times(2)).validateForProcessing(any());
        verify(processor).processTransaction(transaction1);
        verify(processor).processTransaction(transaction2);
        verify(errorHandler).handleUnexpectedError(eq(transaction1), any(Exception.class));
    }

    private Transaction createTransaction(TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setStatus(status);
        return transaction;
    }
}