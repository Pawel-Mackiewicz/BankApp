package info.mackiewicz.bankapp.transaction.service.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.transaction.model.TransactionType;

/**
 * Tests for the TransactionCommandRegistry class.
 */
@ExtendWith(MockitoExtension.class)
class TransactionCommandRegistryTest {

    @Mock
    private TransactionExecutor depositCommand;

    @Mock
    private TransactionExecutor withdrawalCommand;

    private TransactionExecutorRegistry registry;

    @BeforeEach
    void setUp() {
        // Configure mocks
        when(depositCommand.getTransactionType()).thenReturn(TransactionType.DEPOSIT);
        when(withdrawalCommand.getTransactionType()).thenReturn(TransactionType.WITHDRAWAL);

        // Create a registry with our mock commands
        List<TransactionExecutor> commands = Arrays.asList(depositCommand, withdrawalCommand);
        registry = new TransactionExecutorRegistry(commands);
    }

    @Test
    void getCommand_WithRegisteredType_ShouldReturnCorrectCommand() {
        // Act
        TransactionExecutor result = registry.getCommand(TransactionType.DEPOSIT);
        
        // Assert
        assertSame(depositCommand, result);
        // Remove this verification as getTransactionType() is only called during registry initialization
        // not when getCommand() is called
    }

    @Test
    void getCommand_WithUnregisteredType_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> registry.getCommand(TransactionType.TRANSFER_EXTERNAL)
        );
        
        assertEquals("No command registered for transaction type: TRANSFER_EXTERNAL", exception.getMessage());
    }

    @Test
    void constructor_WithDuplicateTransactionTypes_ShouldUseLastRegisteredCommand() {
        // Arrange
        TransactionExecutor anotherDepositCommand = mock(TransactionExecutor.class);
        when(anotherDepositCommand.getTransactionType()).thenReturn(TransactionType.DEPOSIT);
        
        // Create a registry with duplicate commands for the same type
        List<TransactionExecutor> commands = Arrays.asList(
            depositCommand, withdrawalCommand, anotherDepositCommand
        );
        
        // Act
        TransactionExecutorRegistry newRegistry = new TransactionExecutorRegistry(commands);
        TransactionExecutor result = newRegistry.getCommand(TransactionType.DEPOSIT);
        
        // Assert - the last registered command should win
        assertSame(anotherDepositCommand, result);
    }
}