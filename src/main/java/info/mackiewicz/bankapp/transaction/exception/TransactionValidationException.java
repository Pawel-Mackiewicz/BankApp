package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionValidationException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_VALIDATION_ERROR;

    /**
     * Constructs a new TransactionValidationException with the specified detail message.
     *
     * <p>This exception is used to signal a transaction validation error and is initialized with a predefined error code.</p>
     *
     * @param message the detail message explaining the reason for the exception
     */
    public TransactionValidationException(String message) {
        super(message, ERROR_CODE);
    }

    
    /**
     * Constructs a new TransactionValidationException with the specified detail message and cause.
     * <p>
     * This exception is associated with the transaction validation error code.
     *
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
