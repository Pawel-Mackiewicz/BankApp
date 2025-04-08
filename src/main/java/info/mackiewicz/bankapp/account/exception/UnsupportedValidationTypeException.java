package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UnsupportedValidationTypeException extends ValidationBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    /**
     * Constructs a new UnsupportedValidationTypeException with the specified detail message and cause.
     *
     * <p>This exception is initialized with a predefined internal error code indicating an internal error related to unsupported validation types.</p>
     *
     * @param message the detail message explaining the exception
     * @param cause the underlying cause of the exception, or {@code null} if none
     */
    public UnsupportedValidationTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    /**
     * Constructs a new UnsupportedValidationTypeException with the specified detail message.
     *
     * <p>The exception is initialized with the error code {@link ErrorCode#INTERNAL_ERROR}.</p>
     *
     * @param message the detail message explaining the error
     */
    public UnsupportedValidationTypeException(String message) {
        super(message, ERROR_CODE);
    }

}
