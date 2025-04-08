package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when transaction validation fails
 */
public class TransactionAccountConflictException extends TransactionBaseException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_ACCOUNT_CONFLICT;
    
    /**
     * Constructs a new TransactionAccountConflictException with the specified detail message.
     *
     * @param message the detail message explaining the account conflict encountered during transaction validation
     */
    public TransactionAccountConflictException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new TransactionAccountConflictException with the specified detail message and cause.
     *
     * <p>This exception signals a conflict in transaction account validation.
     *
     * @param message the detail message describing the account conflict
     * @param cause the underlying cause of this exception
     */
    public TransactionAccountConflictException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}