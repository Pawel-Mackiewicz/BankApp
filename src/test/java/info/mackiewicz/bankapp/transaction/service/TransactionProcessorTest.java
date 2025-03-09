package info.mackiewicz.bankapp.transaction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class TransactionProcessorTest {

    @Mock
    private StrategyResolver strategyResolver;

    @Mock
    private AccountLockManager accountLockManager;

    @Mock
    private TransactionValidator validator;

    @Mock
    private TransactionErrorHandler errorHandler;

    @Mock
    private TransactionStatusManager statusManager;

    @Mock
    private TransactionStrategy transactionStrategy;

    @InjectMocks
    private TransactionProcessor processor;

    private Transaction transaction;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        sourceAccount = mock(Account.class);
        destinationAccount = mock(Account.class);
        when(sourceAccount.getId()).thenReturn(1);
        when(destinationAccount.getId()).thenReturn(2);
        
        transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        
        when(strategyResolver.resolveStrategy(any(Transaction.class)))
            .thenReturn(transactionStrategy);
    }

    @Test
    void processTransaction_WhenSuccessful_ShouldExecuteAllSteps() {
        // given
        when(transactionStrategy.execute(transaction)).thenReturn(true);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(errorHandler, never()).handleInsufficientFundsError(any(), any());
        verify(errorHandler, never()).handleValidationError(any(), any());
        verify(errorHandler, never()).handleExecutionError(any(), any());
        verify(errorHandler, never()).handleUnexpectedError(any(), any());
    }

    @Test
    void processTransaction_WhenValidationFails_ShouldHandleError() {
        // given
        TransactionValidationException exception = new TransactionValidationException("Invalid transaction");
        doThrow(exception).when(validator).validate(transaction);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(errorHandler).handleValidationError(transaction, exception);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(transactionStrategy, never()).execute(any());
        verify(statusManager, never()).setTransactionStatus(any(), eq(TransactionStatus.DONE));
    }

    @Test
    void processTransaction_WhenInsufficientFunds_ShouldHandleError() {
        // given
        InsufficientFundsException exception = new InsufficientFundsException("Insufficient funds");
        doThrow(exception).when(validator).validate(transaction);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(errorHandler).handleInsufficientFundsError(transaction, exception);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(transactionStrategy, never()).execute(any());
        verify(statusManager, never()).setTransactionStatus(any(), eq(TransactionStatus.DONE));
    }

    @Test
    void processTransaction_WhenExecutionFails_ShouldHandleError() {
        // given
        when(transactionStrategy.execute(transaction)).thenReturn(false);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(errorHandler).handleExecutionError(eq(transaction), any(TransactionExecutionException.class));
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(statusManager, never()).setTransactionStatus(any(), eq(TransactionStatus.DONE));
    }

    @Test
    void processTransaction_WhenUnexpectedError_ShouldHandleError() {
        // given
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(transactionStrategy.execute(transaction)).thenThrow(exception);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(errorHandler).handleUnexpectedError(transaction, exception);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(statusManager, never()).setTransactionStatus(any(), eq(TransactionStatus.DONE));
    }

    @Test
    void processTransaction_ShouldReleaseLocks_EvenIfStatusUpdateFails() {
        // given
        when(transactionStrategy.execute(transaction)).thenReturn(true);
        RuntimeException exception = new RuntimeException("Status update failed");
        doThrow(exception).when(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(errorHandler).handleUnexpectedError(transaction, exception);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
    }

    @Test
    void processTransaction_WhenOneAccountNull_ShouldSucceed() {
        // given
        transaction.setSourceAccount(null);  // only source is null
        transaction.setDestinationAccount(destinationAccount);
        when(transactionStrategy.execute(transaction)).thenReturn(true);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(null, destinationAccount);
        verify(validator).validate(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(accountLockManager).unlockAccounts(null, destinationAccount);
        verify(errorHandler, never()).handleUnexpectedError(any(), any());
    }

    @Test
    void processTransaction_WhenBothAccountsNull_ShouldThrowError() {
        // given
        transaction.setSourceAccount(null);
        transaction.setDestinationAccount(null);
        TransactionValidationException expectedException = new TransactionValidationException("Both accounts cannot be null");
        doThrow(expectedException).when(validator).validate(transaction);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(null, null);
        verify(validator).validate(transaction);
        verify(errorHandler).handleValidationError(transaction, expectedException);
        verify(accountLockManager).unlockAccounts(null, null);
        verify(transactionStrategy, never()).execute(any());
        verify(statusManager, never()).setTransactionStatus(any(), any(TransactionStatus.class));
    }

    @Test
    void processTransaction_WhenLockAcquisitionFails_ShouldHandleError() {
        // given
        AccountLockException exception = new AccountLockException(
            "Thread was interrupted while trying to acquire lock",
            sourceAccount.getId(),
            1,    // attempts made before interruption
            200,  // totalWaitTime in ms
            true  // wasInterrupted
        );
        
        // Mock the lock acquisition to throw immediately to simulate thread interruption
        doThrow(exception).when(accountLockManager).lockAccounts(any(), any());

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(errorHandler).handleLockError(transaction, exception);
        verify(validator, never()).validate(transaction);
        verify(transactionStrategy, never()).execute(transaction);
        verify(statusManager, never()).setTransactionStatus(any(), any());
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
    }

    @Test
    void processTransaction_ShouldPreserveExecutionOrder() {
        // given
        when(transactionStrategy.execute(transaction)).thenReturn(true);
        
        // Create ordered verifiers for strict order checking
        InOrder orderVerifier = inOrder(
            accountLockManager, 
            validator, 
            statusManager, 
            transactionStrategy, 
            errorHandler
        );

        // when
        processor.processTransaction(transaction);

        // then
        orderVerifier.verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        orderVerifier.verify(validator).validate(transaction);
        orderVerifier.verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        orderVerifier.verify(transactionStrategy).execute(transaction);
        orderVerifier.verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        orderVerifier.verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verifyNoInteractions(errorHandler);
    }

    @Test
    void processTransaction_WhenUnlockFails_ShouldStillHandleMainError() {
        // given
        TransactionValidationException validationException = new TransactionValidationException("Validation failed");
        RuntimeException unlockException = new RuntimeException("Unlock failed");
        
        doThrow(validationException).when(validator).validate(transaction);
        doThrow(unlockException).when(accountLockManager).unlockAccounts(any(), any());

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(validator).validate(transaction);
        verify(errorHandler).handleValidationError(transaction, validationException);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        // Should still handle the original validation error even if unlock fails
        verify(errorHandler, never()).handleUnexpectedError(transaction, unlockException);
    }
}