package info.mackiewicz.bankapp.transaction.service.execution.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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
 * Tests for the OwnTransferCommand class.
 */
@ExtendWith(MockitoExtension.class)
class OwnTransferCommandTest {

    @Mock
    private AccountService accountService;
    
    @Mock
    private Account sourceAccount;
    
    @Mock
    private Account destinationAccount;
    
    @Mock
    private Transaction transaction;

    private OwnTransferCommand command;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        command = new OwnTransferCommand();
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
    void getTransactionType_ShouldReturnTransferOwn() {
        // Act
        TransactionType result = command.getTransactionType();
        
        // Assert
        assertEquals(TransactionType.TRANSFER_OWN, result);
    }
}