package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionValidationException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_VALIDATION_ERROR;

    /**
     * Constructs a new TransactionValidationException with the specified detail message.
     *
     * <p>This exception is used to indicate a transaction validation error and is automatically associated with a predefined error code.</p>
     *
     * @param message the detailed error message for this exception
     */
    public TransactionValidationException(String message) {
        super(message, ERROR_CODE);
    }

    
    /**
     * Constructs a new TransactionValidationException with the specified detail message and cause.
     * This exception is instantiated using the predefined error code for transaction validation errors.
     *
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
