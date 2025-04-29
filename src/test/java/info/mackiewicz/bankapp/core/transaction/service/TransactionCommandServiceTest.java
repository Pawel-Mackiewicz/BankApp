package info.mackiewicz.bankapp.core.transaction.service;

import info.mackiewicz.bankapp.core.transaction.exception.TransactionDeletionForbiddenException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.core.transaction.validation.TransactionValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionCommandService.
 * Tests focus on transaction creation and deletion operations.
 */
@Slf4j
class TransactionCommandServiceTest {

    @Mock
    private TransactionRepository repository;

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
        Transaction result = commandService.registerTransaction(transaction);

        // then
        verify(validator).validate(transaction);
        verify(repository).save(transaction);
        assertEquals(transaction, result);
    }

    @Test
    void createTransaction_WhenOwnTransfer_ShouldSaveAndProcess() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER_OWN);
        when(repository.save(transaction)).thenReturn(transaction);

        // when
        Transaction result = commandService.registerTransaction(transaction);

        // then
        verify(validator).validate(transaction);
        verify(repository).save(transaction);
        assertEquals(transaction, result);
    }

    @Test
    void createTransaction_WhenValidationFails_ShouldThrowException() {
        // given
        Transaction transaction = new Transaction();
        doThrow(IllegalArgumentException.class).when(validator).validate(transaction);

        // when/then
        assertThrows(IllegalArgumentException.class, () -> commandService.registerTransaction(transaction));
        verify(repository, never()).save(any());
    }

    @Test
    void deleteTransactionById_WhenTransactionExistsAndStatusNull_ShouldDelete() {
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
    void deleteTransactionById_WhenTransactionExistsAndStatusNEW_ShouldDelete() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.NEW);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when
        commandService.deleteTransactionById(transactionId);

        // then
        verify(queryService).getTransactionById(transactionId);
        verify(repository).delete(transaction);
    }

    @Test
    void deleteTransactionById_WhenTransactionExistsAndStatusDONE_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.DONE);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when/then
        assertThrows(TransactionDeletionForbiddenException.class,
                () -> commandService.deleteTransactionById(transactionId));

        verify(queryService).getTransactionById(transactionId);
        verifyNoInteractions(repository);
    }

    @Test
    void deleteTransactionById_WhenTransactionExistsAndStatusERROR_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.EXECUTION_ERROR);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when/then
        assertThrows(TransactionDeletionForbiddenException.class,
                () -> commandService.deleteTransactionById(transactionId));

        verify(queryService).getTransactionById(transactionId);
        verifyNoInteractions(repository);
    }

    @Test
    void deleteTransactionById_WhenTransactionExistsAndStatusPENDING_ShouldThrowException() {
        // given
        int transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PENDING);
        when(queryService.getTransactionById(transactionId)).thenReturn(transaction);

        // when/then
        assertThrows(TransactionDeletionForbiddenException.class,
                () -> commandService.deleteTransactionById(transactionId));

        verify(queryService).getTransactionById(transactionId);
        verifyNoInteractions(repository);
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

        verifyNoInteractions(repository);
    }
}