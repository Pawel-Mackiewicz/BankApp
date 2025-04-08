package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class InvalidSourceTypeException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new InvalidSourceTypeException with the specified detail message.
     * The associated error code is automatically set to {@link ErrorCode#INTERNAL_ERROR}.
     *
     * @param message the detail message for this exception.
     */
    public InvalidSourceTypeException(String message) {
        super(message, ERROR_CODE);
    }
    /**
     * Constructs a new InvalidSourceTypeException with the specified detail message and cause.
     *
     * <p>This exception is associated with a predefined internal error code.</p>
     *
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public InvalidSourceTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
