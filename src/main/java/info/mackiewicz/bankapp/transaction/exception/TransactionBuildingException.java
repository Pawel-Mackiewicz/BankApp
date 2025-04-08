package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionBuildingException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new TransactionBuildingException with the specified detail message.
     *
     * <p>This exception is thrown to indicate an error during the construction of a transaction.
     * It categorizes the error using a pre-set internal error code.</p>
     *
     * @param message the detail message explaining the error condition
     */
    public TransactionBuildingException(String message) {
        super(message, ERROR_CODE);
    }
    /**
     * Constructs a new TransactionBuildingException with the specified detail message and underlying cause.
     *
     * <p>This constructor sets the error code to {@code ErrorCode.INTERNAL_ERROR}.</p>
     *
     * @param message the detail message explaining the exception
     * @param cause   the underlying cause of the exception
     */
    public TransactionBuildingException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
