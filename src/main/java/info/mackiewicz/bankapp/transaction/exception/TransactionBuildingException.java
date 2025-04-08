package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionBuildingException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new TransactionBuildingException with the specified error message.
     *
     * <p>This constructor creates an exception that encapsulates a detailed error message along
     * with a predefined internal error code, facilitating consistent error handling during
     * transaction building.</p>
     *
     * @param message the detailed error message
     */
    public TransactionBuildingException(String message) {
        super(message, ERROR_CODE);
    }
    /**
     * Constructs a new TransactionBuildingException with the specified detail message and cause.
     *
     * <p>This exception signals an error encountered during transaction building. The error code is automatically 
     * set to {@code ErrorCode.INTERNAL_ERROR}.</p>
     *
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public TransactionBuildingException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
