package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UnsupportedValidationTypeException extends ValidationBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new UnsupportedValidationTypeException with the specified detail message and cause.
     *
     * <p>This exception is thrown when an unsupported validation type is encountered, encapsulating
     * the provided message and underlying cause. It is associated with a predefined internal error code.
     *
     * @param message a detailed message describing the error
     * @param cause the underlying reason for the exception, or {@code null} if none
     */
    public UnsupportedValidationTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    /**
     * Constructs a new UnsupportedValidationTypeException with the specified detail message.
     *
     * @param message the detail message explaining the unsupported validation type.
     */
    public UnsupportedValidationTypeException(String message) {
        super(message, ERROR_CODE);
    }

}
