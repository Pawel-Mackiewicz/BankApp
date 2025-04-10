package info.mackiewicz.bankapp.transaction.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.exception.TransactionAccountConflictException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.user.model.User;

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

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void validate_WithValidDeposit_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDestinationAccount(destinationAccount);

        // When & Then
        assertDoesNotThrow(() -> validator.validate(transaction));
    }

    @Test
    void validate_WithValidWithdrawal_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);

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