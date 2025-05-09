package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for the FeeTransactionExecutor class.
 */
@ExtendWith(MockitoExtension.class)
class FeeTransactionExecutorTest {

    @Mock
    private AccountService accountService;
    
    @Mock
    private Account sourceAccount;
    
    @Mock
    private Account destinationAccount;
    
    @Mock
    private Account bankAccount;
    
    @Mock
    private Transaction transaction;

    private FeeTransactionExecutor command;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        command = new FeeTransactionExecutor();
        amount = new BigDecimal("10.00");
    }

    @Test
    void execute_WithDestinationAccount_ShouldWithdrawAndDeposit() {
        // Arrange
        when(transaction.getSourceAccount()).thenReturn(sourceAccount);
        when(transaction.getDestinationAccount()).thenReturn(destinationAccount);
        when(transaction.getAmount()).thenReturn(amount);
        
        // Act
        command.execute(transaction, accountService);
        
        // Assert
        verify(accountService).withdraw(sourceAccount, amount);
        verify(accountService).deposit(destinationAccount, amount);
        verify(transaction, never()).setDestinationAccount(any());
        verify(accountService, never()).getAccountById(anyInt());
    }

    @Test
    void execute_WithoutDestinationAccount_ShouldUseAndSetBankAccount() {
        // Arrange
        when(transaction.getSourceAccount()).thenReturn(sourceAccount);
        when(transaction.getDestinationAccount()).thenReturn(null);
        when(transaction.getAmount()).thenReturn(amount);
        when(accountService.getAccountById(-1)).thenReturn(bankAccount);
        
        // Act
        command.execute(transaction, accountService);
        
        // Assert
        InOrder inOrder = inOrder(accountService, transaction);
        inOrder.verify(accountService).getAccountById(-1);
        inOrder.verify(transaction).setDestinationAccount(bankAccount);
        inOrder.verify(accountService).withdraw(sourceAccount, amount);
        inOrder.verify(accountService).deposit(bankAccount, amount);
    }

    @Test
    void getTransactionType_ShouldReturnFee() {
        // Act
        TransactionType result = command.getTransactionType();
        
        // Assert
        assertEquals(TransactionType.FEE, result);
    }
}