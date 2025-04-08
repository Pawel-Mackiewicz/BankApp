package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when transaction validation fails
 */
public class TransactionAccountConflictException extends TransactionBaseException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_ACCOUNT_CONFLICT;
    
    /**
     * Constructs a new TransactionAccountConflictException with the specified detail message.
     * <p>
     * This exception is thrown when a transaction fails due to an account conflict.
     * </p>
     *
     * @param message the detail message explaining the cause of the conflict
     */
    public TransactionAccountConflictException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new TransactionAccountConflictException with the specified detail message and cause.
     * This exception indicates a conflict in transaction processing related to account validation.
     *
     * @param message the detail message explaining the exception
     * @param cause the underlying cause of the exception
     */
    public TransactionAccountConflictException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}