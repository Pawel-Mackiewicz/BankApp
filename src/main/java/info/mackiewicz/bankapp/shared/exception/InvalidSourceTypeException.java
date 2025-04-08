package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class InvalidSourceTypeException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new InvalidSourceTypeException with the specified error message.
     *
     * <p>This exception is thrown when an invalid source type is encountered, automatically
     * incorporating a predefined internal error code.
     *
     * @param message the detail message providing context for the error
     */
    public InvalidSourceTypeException(String message) {
        super(message, ERROR_CODE);
    }
    /**
     * Constructs a new InvalidSourceTypeException with the specified detail message and cause.
     *
     * <p>This exception is thrown when an invalid source type is encountered.
     *
     * @param message the detail message describing the invalid source type error
     * @param cause the underlying exception that triggered this error
     */
    public InvalidSourceTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
