package info.mackiewicz.bankapp.transaction.service.execution.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

/**
 * Tests for the DepositTransactionCommand class.
 */
@ExtendWith(MockitoExtension.class)
class DepositTransactionCommandTest {

    @Mock
    private AccountService accountService;
    
    @Mock
    private Account destinationAccount;
    
    @Mock
    private Transaction transaction;

    private DepositTransactionCommand command;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        command = new DepositTransactionCommand();
        amount = new BigDecimal("100.00");
    }

    @Test
    void execute_ShouldDepositToDestinationAccount() {
        // Arrange
        when(transaction.getDestinationAccount()).thenReturn(destinationAccount);
        when(transaction.getAmount()).thenReturn(amount);
        
        // Act
        command.execute(transaction, accountService);
        
        // Assert
        verify(accountService).deposit(destinationAccount, amount);
        verify(transaction).getDestinationAccount();
        verify(transaction).getAmount();
        verifyNoMoreInteractions(accountService);
    }

    @Test
    void getTransactionType_ShouldReturnDeposit() {
        // Act
        TransactionType result = command.getTransactionType();
        
        // Assert
        assertEquals(TransactionType.DEPOSIT, result);
    }
}