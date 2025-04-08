package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UnsupportedValidationTypeException extends ValidationBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new UnsupportedValidationTypeException with the specified detail message and cause.
     * <p>
     * This exception signals that a provided validation type is unsupported and is associated with an internal error code.
     * </p>
     *
     * @param message the detail message explaining the exception
     * @param cause   the underlying cause of the exception
     */
    public UnsupportedValidationTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    /**
     * Constructs a new UnsupportedValidationTypeException with the specified detail message.
     * The provided message is passed to the superclass constructor along with an internal error code.
     *
     * @param message the detail message that explains the cause of the exception
     */
    public UnsupportedValidationTypeException(String message) {
        super(message, ERROR_CODE);
    }

}
