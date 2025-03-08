package info.mackiewicz.bankapp.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.mackiewicz.bankapp.shared.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for TransactionCommandService.
 * Tests focus on transaction creation and deletion operations.
 */
@Slf4j
class TransactionCommandServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionProcessor processor;

    @Mock
    private TransactionValidator validator;

    @Mock
    private TransactionQueryService queryService;

    @InjectMocks
    private TransactionCommandService commandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_WhenNormalTransaction_ShouldSaveWithoutProcessing() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        when(repository.save(transaction)).thenReturn(transaction);

        // when
        Transaction result = commandService.createTransaction(transaction);

        // then
        verify(validator).validate(transaction);
        verify(repository).save(transaction);
        verify(processor, never()).processTransaction(any());
        assertEquals(transaction, result);
    }

    @Test
    void createTransaction_WhenOwnTransfer_ShouldSaveAndProcess() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER_OWN);
        when(repository.save(transaction)).thenReturn(transaction);

        // when
        Transaction result = commandService.createTransaction(transaction);

        // then
        verify(validator).validate(transaction);
        verify(repository).save(transaction);
        verify(processor).processTransaction(transaction);
        assertEquals(transaction, result);
    }

    @Test
    void createTransaction_WhenValidationFails_ShouldThrowException() {
        // given
        Transaction transaction = new Transaction();
        doThrow(IllegalArgumentException.class).when(validator).validate(transaction);

        // when/then
        assertThrows(IllegalArgumentException.class, () -> commandService.createTransaction(transaction));
        verify(repository, never()).save(any());
        verify(processor, never()).processTransaction(any());
    }

    @Test
    void deleteTransactionById_WhenTransactionExists_ShouldDelete() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when
        commandService.deleteTransactionById(transactionId);

        // then
        verify(queryService).getTransactionById(transactionId);
        verify(repository).delete(transaction);
    }

    @Test
    void deleteTransactionById_WhenTransactionDoesNotExist_ShouldPropagateException() {
        // given
        int transactionId = 1;
        when(queryService.getTransactionById(transactionId))
            .thenThrow(new TransactionNotFoundException("Not found"));

        // when/then
        assertThrows(TransactionNotFoundException.class, 
            () -> commandService.deleteTransactionById(transactionId));
        verify(repository, never()).delete(any());
    }
}