package info.mackiewicz.bankapp.core.transaction.validation;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.core.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionAccountConflictException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTransactionValidatorTest {

    private DefaultTransactionValidator validator;
    private Transaction transaction;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        validator = new DefaultTransactionValidator();
        transaction = new Transaction();
        sourceAccount = TestAccountBuilder.createTestAccountWithRandomOwner();
        destinationAccount = TestAccountBuilder.createTestAccountWithRandomOwner();
    }

    @Test
    void validate_WithValidTransfer_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void validate_WithValidDeposit_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void validate_WithValidWithdrawal_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void validate_WithNullAmount_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);

        // When & Then
        assertThrows(TransactionValidationException.class, 
            () -> validator.validate(transaction),
            "Transaction amount cannot be null");
    }

    @Test
    void validate_WithZeroAmount_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);

        // When & Then
        assertThrows(TransactionValidationException.class, 
            () -> validator.validate(transaction),
            "Transaction amount must be positive");
    }

    @Test
    void validate_WithNullType_ShouldThrowException() {
        // Given
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);

        // When & Then
        assertThrows(TransactionValidationException.class, 
            () -> validator.validate(transaction),
            "Transaction type cannot be null");
    }

    @Test
    void validate_WithInsufficientFunds_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(BigDecimal.valueOf(10000));
        transaction.setSourceAccount(sourceAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When & Then
        assertThrows(InsufficientFundsException.class,
                () -> validator.validate(transaction),
                "Insufficient funds for transaction");
    }

    @Test
    void validate_WithInvalidTitle_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle("Invalid#<>Title!"); // Contains invalid characters

        // When & Then
        assertThrows(TransactionValidationException.class,
                () -> validator.validate(transaction),
                "Transaction title contains invalid characters");
    }

    @Test
    void validate_WithValidTitle_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void validate_TransferWithNullSourceAccount_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDestinationAccount(destinationAccount);

        // When & Then
        assertThrows(TransactionValidationException.class,
            () -> validator.validate(transaction),
            "Transfer transaction must have a source account");
    }

    @Test
    void validate_TransferWithNullDestinationAccount_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);

        // When & Then
        assertThrows(TransactionValidationException.class,
            () -> validator.validate(transaction),
            "Transfer transaction must have a destination account");
    }

    @Test
    void validate_TransferWithSameAccounts_ShouldThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(sourceAccount);

        // When & Then
        assertThrows(TransactionAccountConflictException.class,
            () -> validator.validate(transaction),
            "Source and destination accounts cannot be the same");
    }

    @Test
    void validate_OwnTransferWithSameAccounts_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_OWN);
        transaction.setAmount(BigDecimal.TEN);

        User owner = TestUserBuilder.createTestUser();
        
        // Create two accounts with the same owner for own transfer
        sourceAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        destinationAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
      
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void isValid_WithValidTransaction_ShouldReturnTrue() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle("Valid Title"); // Properly formatted title

        // When
        boolean result = validator.isValid(transaction);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_WithInvalidTransaction_ShouldReturnFalse() {
        // Given
        transaction.setType(TransactionType.TRANSFER_INTERNAL);
        transaction.setAmount(BigDecimal.ZERO);

        // When
        boolean result = validator.isValid(transaction);

        // Then
        assertFalse(result);
    }
}