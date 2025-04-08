package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionBuildingException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new TransactionBuildingException with the specified detail message.
     *
     * @param message the detail message describing the exception
     */
    public TransactionBuildingException(String message) {
        super(message, ERROR_CODE);
    }
    /**
     * Constructs a new TransactionBuildingException with the specified detail message and cause.
     *
     * <p>This constructor associates the exception with a predefined error code ({@link #ERROR_CODE}).</p>
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception
     */
    public TransactionBuildingException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
