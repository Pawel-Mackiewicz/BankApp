package info.mackiewicz.bankapp.transaction.service.error;

import info.mackiewicz.bankapp.core.account.exception.AccountLockException;
import info.mackiewicz.bankapp.core.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.system.transaction.processing.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.system.transaction.processing.error.TransactionErrorNotifier;
import info.mackiewicz.bankapp.system.transaction.processing.helpers.TransactionStatusManager;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for TransactionErrorHandler.
 * Tests focus on error handling logic and proper status updates.
 */
@Slf4j
class TransactionErrorHandlerTest {

    @Mock
    private TransactionStatusManager statusManager;

    @Mock
    private TransactionErrorNotifier errorNotifier;

    @InjectMocks
    private TransactionErrorHandler errorHandler;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transaction = new Transaction();
        transaction.setId(1);
    }

    @Test
    @DisplayName("Should handle insufficient funds errors")
    void handleInsufficientFundsError_ShouldSetProperStatusAndNotify() {
        // given
        InsufficientFundsException exception = new InsufficientFundsException("Insufficient funds");

        // when
        errorHandler.handleInsufficientFundsError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.INSUFFICIENT_FUNDS);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle validation errors")
    void handleValidationError_ShouldSetProperStatusAndNotify() {
        // given
        Exception exception = new Exception("Validation error");

        // when
        errorHandler.handleValidationError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.VALIDATION_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle unexpected errors")
    void handleUnexpectedError_ShouldSetProperStatusAndNotify() {
        // given
        Exception exception = new RuntimeException("Unexpected error");

        // when
        errorHandler.handleUnexpectedError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle lock acquisition errors")
    void handleLockError_ShouldSetProperStatusAndNotify() {
        // given
        AccountLockException exception = new AccountLockException(
                "Failed to acquire lock", 
                123, 
                3, 
                1000, 
                false);

        // when
        errorHandler.handleLockError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle unexpected lock-related errors")
    void handleUnexpectedLockError_ShouldSetProperStatusAndNotify() {
        // given
        Exception exception = new RuntimeException("Unexpected lock error");

        // when
        errorHandler.handleUnexpectedLockError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle unlock errors")
    void handleUnlockError_ShouldSetProperStatusAndNotify() {
        // given
        AccountUnlockException exception = new AccountUnlockException("Failed to release lock", 123);

        // when
        errorHandler.handleUnlockError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle unexpected unlock-related errors")
    void handleUnexpectedUnlockError_ShouldSetProperStatusAndNotify() {
        // given
        Exception exception = new RuntimeException("Unexpected unlock error");

        // when
        errorHandler.handleUnexpectedUnlockError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }

    @Test
    @DisplayName("Should handle transaction status change errors")
    void handleTransactionStatusChangeError_ShouldSetProperStatusAndNotify() {
        // given
        Exception exception = new RuntimeException("Status update failed");

        // when
        errorHandler.handleTransactionStatusChangeError(transaction, exception);

        // then
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        verify(errorNotifier).notifyError(transaction, exception);
    }
}