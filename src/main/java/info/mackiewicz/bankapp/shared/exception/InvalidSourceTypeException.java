package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class InvalidSourceTypeException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new InvalidSourceTypeException with the specified detail message.
     * The error code is automatically set to ErrorCode.INTERNAL_ERROR.
     *
     * @param message the detail message describing the exception
     */
    public InvalidSourceTypeException(String message) {
        super(message, ERROR_CODE);
    }
    /**
     * Constructs a new InvalidSourceTypeException with the specified detail message and cause.
     *
     * <p>This exception indicates that an invalid source type was encountered and is associated with an internal error.</p>
     *
     * @param message the detail message explaining the error
     * @param cause the underlying cause of the exception, or {@code null} if none
     */
    public InvalidSourceTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
