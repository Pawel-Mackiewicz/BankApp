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
import static org.mockito.Mockito.*;

/**
 * Tests for the WithdrawalTransactionExecutor class.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalTransactionExecutorTest {

    @Mock
    private AccountService accountService;
    
    @Mock
    private Account sourceAccount;
    
    @Mock
    private Transaction transaction;

    private WithdrawalTransactionExecutor command;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        command = new WithdrawalTransactionExecutor();
        amount = new BigDecimal("100.00");
    }

    @Test
    void execute_ShouldWithdrawFromSourceAccount() {
        // Arrange
        when(transaction.getSourceAccount()).thenReturn(sourceAccount);
        when(transaction.getAmount()).thenReturn(amount);
        
        // Act
        command.execute(transaction, accountService);
        
        // Assert
        verify(accountService).withdraw(sourceAccount, amount);
        verify(transaction).getSourceAccount();
        verify(transaction).getAmount();
        verifyNoMoreInteractions(accountService);
    }

    @Test
    void getTransactionType_ShouldReturnWithdrawal() {
        // Act
        TransactionType result = command.getTransactionType();
        
        // Assert
        assertEquals(TransactionType.WITHDRAWAL, result);
    }
}