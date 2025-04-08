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
     * <p>This exception is thrown when a conflict occurs during transaction validation related
     * to account issues.
     *
     * @param message the detail message providing context for the exception
     */
    public TransactionAccountConflictException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new TransactionAccountConflictException with the specified detail message and cause.
     *
     * <p>The error code is implicitly set to {@link ErrorCode#TRANSACTION_ACCOUNT_CONFLICT}.</p>
     *
     * @param message the detail message
     * @param cause the underlying cause of this exception
     */
    public TransactionAccountConflictException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}