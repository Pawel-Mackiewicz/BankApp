package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the InternalTransferCommand class.
 */
@ExtendWith(MockitoExtension.class)
class InternalTransferCommandTest {

    @Mock
    private AccountService accountService;
    
    @Mock
    private Account sourceAccount;
    
    @Mock
    private Account destinationAccount;
    
    @Mock
    private Transaction transaction;

    private InternalTransferExecutor command;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        command = new InternalTransferExecutor();
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
    }

    @Test
    void getTransactionType_ShouldReturnTransferInternal() {
        // Act
        TransactionType result = command.getTransactionType();
        
        // Assert
        assertEquals(TransactionType.TRANSFER_INTERNAL, result);
    }
}