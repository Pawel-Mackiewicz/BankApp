package info.mackiewicz.bankapp.system.transaction.processing.core.execution.base;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

/**
 * Tests for the BaseTransferCommand class.
 */
@ExtendWith(MockitoExtension.class)
class BaseTransferCommandTest {

    @Mock
    private AccountService accountService;
    
    @Mock
    private Account sourceAccount;
    
    @Mock
    private Account destinationAccount;
    
    @Mock
    private Transaction transaction;

    private TestableBaseTransferCommand command;
    private BigDecimal amount;

    // Concrete implementation of BaseTransferCommand for testing
    static class TestableBaseTransferCommand extends BaseTransferExecutor {
        private TransactionType type = TransactionType.TRANSFER_INTERNAL;
        
        @Override
        public TransactionType getTransactionType() {
            return type;
        }
        
        public void setTransactionType(TransactionType type) {
            this.type = type;
        }
    }

    @BeforeEach
    void setUp() {
        command = new TestableBaseTransferCommand();
        amount = new BigDecimal("100.00");
    }

    @Test
    void execute_ShouldWithdrawFromSourceAndDepositToDestination() {
        // Arrange
        when(transaction.getSourceAccount()).thenReturn(sourceAccount);
        when(transaction.getDestinationAccount()).thenReturn(destinationAccount);
        when(transaction.getAmount()).thenReturn(amount);
        
        // Act
        command.execute(transaction, accountService);
        
        // Assert
        verify(accountService).withdraw(sourceAccount, amount);
        verify(accountService).deposit(destinationAccount, amount);
        verify(transaction).getSourceAccount();
        verify(transaction).getDestinationAccount();
        verify(transaction, times(2)).getAmount();
        verifyNoMoreInteractions(accountService);
    }
}