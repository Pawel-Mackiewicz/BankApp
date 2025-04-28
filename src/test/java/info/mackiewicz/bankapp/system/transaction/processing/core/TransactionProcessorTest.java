package info.mackiewicz.bankapp.system.transaction.processing.core;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import info.mackiewicz.bankapp.account.exception.AccountUnlockException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.system.notification.email.locking.AccountLockManager;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.TransactionExecutor;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.TransactionExecutorRegistry;
import info.mackiewicz.bankapp.system.transaction.processing.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.system.transaction.processing.helpers.TransactionStatusManager;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class TransactionProcessorTest {

    @Mock
    private TransactionExecutorRegistry commandRegistry;

    @Mock
    private AccountLockManager accountLockManager;

    @Mock
    private TransactionStatusManager statusManager;

    @Mock
    private TransactionErrorHandler errorHandler;

    @Mock
    private TransactionExecutor executionCommand;

    @Mock
    private AccountService accountService;

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

        when(commandRegistry.getCommand(any(TransactionType.class)))
                .thenReturn(executionCommand);
    }

    @Test
    void processTransaction_WhenSuccessful_ShouldExecuteAllSteps() {
        // when
        processor.processTransaction(transaction);

        // then
        verify(loggingService).logTransactionAttempt(transaction);
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(loggingService).logLockingAccounts(transaction);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        verify(commandRegistry).getCommand(transaction.getType());
        verify(executionCommand).execute(transaction, accountService);
        verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        verify(loggingService).logSuccessfulTransaction(transaction);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(loggingService).logUnlockingAccounts(transaction);
    }

    @Test
    void processTransaction_WhenInsufficientFunds_ShouldHandleError() {
        // given
        InsufficientFundsException exception = new InsufficientFundsException("Insufficient funds");
        doThrow(exception).when(executionCommand).execute(transaction, accountService);

        // when/then
        try {
            processor.processTransaction(transaction);
            fail("Expected InsufficientFundsException to be thrown");
        } catch (InsufficientFundsException e) {
            // Expected exception
            verify(loggingService).logTransactionAttempt(transaction);
            verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logLockingAccounts(transaction);
            verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
            verify(commandRegistry).getCommand(transaction.getType());
            verify(executionCommand).execute(transaction, accountService);
            verify(errorHandler).handleInsufficientFundsError(transaction, exception);
            verify(statusManager, never()).setTransactionStatus(transaction, TransactionStatus.DONE);
            verify(loggingService, never()).logSuccessfulTransaction(transaction);
            verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logUnlockingAccounts(transaction);
        }
    }

    @Test
    void processTransaction_WhenUnexpectedError_ShouldHandleError() {
        // given
        RuntimeException exception = new RuntimeException("Unexpected error");
        doThrow(exception).when(executionCommand).execute(transaction, accountService);

        // when/then
        try {
            processor.processTransaction(transaction);
            fail("Expected TransactionExecutionException to be thrown");
        } catch (TransactionExecutionException e) {
            // Oczekiwane rzucenie TransactionExecutionException
            verify(loggingService).logTransactionAttempt(transaction);
            verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logLockingAccounts(transaction);
            verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
            verify(commandRegistry).getCommand(transaction.getType());
            verify(executionCommand).execute(transaction, accountService);
            verify(errorHandler).handleUnexpectedError(transaction, exception);
            verify(statusManager, never()).setTransactionStatus(transaction, TransactionStatus.DONE);
            verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logUnlockingAccounts(transaction);
            verify(loggingService, never()).logSuccessfulTransaction(transaction);
        }
    }

    @Test
    void processTransaction_WhenStatusUpdateToDoneThrowsException_ShouldHandleErrorCorrectly() {
        // given
        RuntimeException exception = new RuntimeException("Status update failed");
        doThrow(exception).when(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);

        // when/then
        try {
            processor.processTransaction(transaction);
            fail("Expected TransactionExecutionException to be thrown");
        } catch (TransactionExecutionException e) {
            // verify correct behavior
            verify(loggingService).logTransactionAttempt(transaction);
            verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logLockingAccounts(transaction);
            verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
            verify(commandRegistry).getCommand(transaction.getType());
            verify(executionCommand).execute(transaction, accountService);
            verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE); // verify called but throws exception
            verify(errorHandler).handleTransactionStatusChangeError(transaction, exception);
            verify(loggingService, never()).logSuccessfulTransaction(transaction);
            verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logUnlockingAccounts(transaction);
        }
    }

    @Test
    void processTransaction_WhenLockAcquisitionFails_ShouldHandleError() {
        // given
        AccountLockException exception = new AccountLockException(
                "Thread was interrupted while trying to acquire lock",
                sourceAccount.getId(),
                1, // attempts made before interruption
                200, // totalWaitTime in ms
                true // wasInterrupted
        );

        doThrow(exception).when(accountLockManager).lockAccounts(any(), any());

        // when
        processor.processTransaction(transaction);

        // then
        verify(loggingService).logTransactionAttempt(transaction);
        verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        verify(errorHandler).handleLockError(transaction, exception);
        verify(statusManager, never()).setTransactionStatus(any(), any());
        verify(commandRegistry, never()).getCommand(any());
        verify(executionCommand, never()).execute(transaction, accountService);
        verify(loggingService, never()).logLockingAccounts(transaction);
        verify(loggingService, never()).logSuccessfulTransaction(transaction);
        verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        verify(loggingService).logUnlockingAccounts(transaction);
    }

    @Test
    void processTransaction_WhenUnlockFails_ShouldHandleUnlockErrorAndRethrow() {
        // given
        AccountUnlockException exception = new AccountUnlockException(
                "Failed to unlock account",
                sourceAccount.getId());
        doThrow(exception).when(accountLockManager).unlockAccounts(any(), any());

        // when/then
        try {
            processor.processTransaction(transaction);
            fail("Expected AccountUnlockException to be thrown");
        } catch (AccountUnlockException e) {
            // Wyjątek został ponownie rzucony, co jest oczekiwane
            verify(loggingService).logTransactionAttempt(transaction);
            verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
            verify(loggingService).logLockingAccounts(transaction);
            verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
            verify(commandRegistry).getCommand(transaction.getType());
            verify(executionCommand).execute(transaction, accountService);
            verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
            verify(loggingService).logSuccessfulTransaction(transaction);
            verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
            verify(errorHandler).handleUnlockError(transaction, exception);
        }
    }

    @Test
    void processTransaction_ShouldPreserveExecutionOrder() {
        // Create ordered verifier for strict order checking
        InOrder orderVerifier = inOrder(
                loggingService,
                accountLockManager,
                loggingService,
                statusManager,
                commandRegistry,
                executionCommand,
                statusManager,
                loggingService,
                accountLockManager,
                loggingService);

        // when
        processor.processTransaction(transaction);

        // then
        orderVerifier.verify(loggingService).logTransactionAttempt(transaction);
        orderVerifier.verify(accountLockManager).lockAccounts(sourceAccount, destinationAccount);
        orderVerifier.verify(loggingService).logLockingAccounts(transaction);
        orderVerifier.verify(statusManager).setTransactionStatus(transaction, TransactionStatus.PENDING);
        orderVerifier.verify(commandRegistry).getCommand(transaction.getType());
        orderVerifier.verify(executionCommand).execute(transaction, accountService);
        orderVerifier.verify(statusManager).setTransactionStatus(transaction, TransactionStatus.DONE);
        orderVerifier.verify(loggingService).logSuccessfulTransaction(transaction);
        orderVerifier.verify(accountLockManager).unlockAccounts(sourceAccount, destinationAccount);
        orderVerifier.verify(loggingService).logUnlockingAccounts(transaction);
        verifyNoInteractions(errorHandler);
    }
}