package info.mackiewicz.bankapp.transaction.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.mackiewicz.bankapp.shared.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.shared.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;

/**
 * Unit tests for TransactionStatusChecker.
 * Tests focus on status checking logic and validation for processing.
 */
class TransactionStatusCheckerTest {

    private TransactionStatusChecker statusChecker;
    
    @BeforeEach
    void setUp() {
        statusChecker = new TransactionStatusChecker();
    }
    
    @Test
    void canBeProcessed_WithNewStatus_ReturnsTrue() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        
        // when
        boolean result = statusChecker.canBeProcessed(transaction);
        
        // then
        assertTrue(result);
    }
    
    @Test
    void canBeProcessed_WithNonNewStatus_ReturnsFalse() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.PENDING);
        
        // when
        boolean result = statusChecker.canBeProcessed(transaction);
        
        // then
        assertFalse(result);
    }
    
    @Test
    void isInProgress_WithPendingStatus_ReturnsTrue() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.PENDING);
        
        // when
        boolean result = statusChecker.isInProgress(transaction);
        
        // then
        assertTrue(result);
    }
    
    @Test
    void isInProgress_WithNonPendingStatus_ReturnsFalse() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        
        // when
        boolean result = statusChecker.isInProgress(transaction);
        
        // then
        assertFalse(result);
    }
    
    @Test
    void isCompleted_WithDoneStatus_ReturnsTrue() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.DONE);
        
        // when
        boolean result = statusChecker.isCompleted(transaction);
        
        // then
        assertTrue(result);
    }
    
    @Test
    void isCompleted_WithNonDoneStatus_ReturnsFalse() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        
        // when
        boolean result = statusChecker.isCompleted(transaction);
        
        // then
        assertFalse(result);
    }
    
    @Test
    void hasFailed_WithFailedStatus_ReturnsTrue() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.INSUFFICIENT_FUNDS);
        
        // when
        boolean result = statusChecker.hasFailed(transaction);
        
        // then
        assertTrue(result);
    }
    
    @Test
    void hasFailed_WithNonFailedStatus_ReturnsFalse() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        
        // when
        boolean result = statusChecker.hasFailed(transaction);
        
        // then
        assertFalse(result);
    }
    
    @Test
    void validateForProcessing_WithNewStatus_DoesNotThrowException() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.NEW);
        
        // when/then
        assertDoesNotThrow(() -> statusChecker.validateForProcessing(transaction));
    }
    
    @Test
    void validateForProcessing_WithDoneStatus_ThrowsTransactionAlreadyProcessedException() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.DONE);
        
        // when/then
        assertThrows(TransactionAlreadyProcessedException.class, 
                () -> statusChecker.validateForProcessing(transaction));
    }
    
    @Test
    void validateForProcessing_WithPendingStatus_ThrowsUnsupportedOperationException() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.PENDING);
        
        // when/then
        assertThrows(UnsupportedOperationException.class, 
                () -> statusChecker.validateForProcessing(transaction));
    }
    
    @Test
    void validateForProcessing_WithFailedStatus_ThrowsTransactionCannotBeProcessedException() {
        // given
        Transaction transaction = createTransaction(TransactionStatus.SYSTEM_ERROR);
        
        // when/then
        assertThrows(TransactionCannotBeProcessedException.class, 
                () -> statusChecker.validateForProcessing(transaction));
    }
    
    private Transaction createTransaction(TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setStatus(status);
        return transaction;
    }
}