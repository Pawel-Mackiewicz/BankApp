package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionValidationException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_VALIDATION_ERROR;

    /**
     * Constructs a new TransactionValidationException with the specified detail message.
     * <p>
     * This exception indicates a transaction validation error and automatically assigns
     * the predefined error code {@code ErrorCode.TRANSACTION_VALIDATION_ERROR}.
     * </p>
     *
     * @param message a detailed message describing the validation error
     */
    public TransactionValidationException(String message) {
        super(message, ERROR_CODE);
    }

    
    /**
     * Constructs a new TransactionValidationException with the specified detail message and cause.
     * <p>
     * The error code for this exception is set to {@link ErrorCode#TRANSACTION_VALIDATION_ERROR}.
     *
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
