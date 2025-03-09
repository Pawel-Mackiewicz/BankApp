package info.mackiewicz.bankapp.transaction.service;

import static org.mockito.ArgumentMatchers.any;
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
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.error.TransactionFailureHandler;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class TransactionProcessorTest {

    @Mock
    private StrategyResolver strategyResolver;

    @Mock
    private AccountLockManager accountLockManager;

    @Mock
    private TransactionStatusManager statusManager;

    @Mock
    private TransactionFailureHandler errorHandler;
    
    @Mock
    private TransactionStrategy transactionStrategy;

    @Mock
    private LoggingService loggingService;

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
        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verifyNoInteractions(errorHandler);
    }

    @Test
    void processTransaction_WhenInsufficientFunds_ShouldHandleError() {
        // given
        InsufficientFundsException exception = new InsufficientFundsException("Insufficient funds");
        doThrow(exception).when(transactionStrategy).execute(transaction);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(errorHandler).handleInsufficientFundsError(transaction, exception);
        verify(statusManager, never()).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
    }

    @Test
    void processTransaction_WhenUnexpectedError_ShouldHandleError() {
        // given
        RuntimeException exception = new RuntimeException("Unexpected error");
        doThrow(exception).when(transactionStrategy).execute(transaction);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(errorHandler).handleUnexpectedError(transaction, exception); 
        verify(statusManager, never()).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
    }

    @Test
    void processTransaction_ShouldReleaseLocks_EvenIfStatusUpdateFails() {
        // given
        RuntimeException exception = new RuntimeException("Status update failed");
        doThrow(exception).when(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(errorHandler).handleUnexpectedError(transaction, exception);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
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
        
        doThrow(exception).when(accountLockManager).lockAccounts(any(), any());

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(errorHandler).handleLockError(transaction, exception);
        verify(statusManager, never()).setTransactionStatus(any(), any());
        verify(transactionStrategy, never()).execute(transaction);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
    }

    @Test
    void processTransaction_WhenUnlockFails_ShouldHandleUnlockError() {
        // given
        AccountUnlockException exception = new AccountUnlockException(
            "Failed to unlock account", 
            sourceAccount.getId()
        );
        doThrow(exception).when(accountLockManager).unlockAccounts(any(), any());

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(transactionStrategy).execute(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(errorHandler).handleUnlockError(transaction, exception);
    }

    @Test
    void processTransaction_ShouldPreserveExecutionOrder() {
        // Create ordered verifier for strict order checking
        InOrder orderVerifier = inOrder(
            accountLockManager, 
            statusManager, 
            transactionStrategy,
            accountLockManager
        );

        // when
        processor.processTransaction(transaction);

        // then
        orderVerifier.verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        orderVerifier.verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        orderVerifier.verify(transactionStrategy).execute(transaction);
        orderVerifier.verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        orderVerifier.verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verifyNoInteractions(errorHandler);
    }
}