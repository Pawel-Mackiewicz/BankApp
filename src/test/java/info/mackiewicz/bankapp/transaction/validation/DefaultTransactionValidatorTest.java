package info.mackiewicz.bankapp.transaction.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

class DefaultTransactionValidatorTest {

    private DefaultTransactionValidator validator;
    private Transaction transaction;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        validator = new DefaultTransactionValidator();
        transaction = new Transaction();
        sourceAccount = mock(Account.class);
        destinationAccount = mock(Account.class);
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
        assertThrows(TransactionValidationException.class, 
            () -> validator.validate(transaction),
            "Only TRANSFER_OWN can have the same source and destination account");
    }

    @Test
    void validate_OwnTransferWithSameAccounts_ShouldNotThrowException() {
        // Given
        transaction.setType(TransactionType.TRANSFER_OWN);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(sourceAccount);

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