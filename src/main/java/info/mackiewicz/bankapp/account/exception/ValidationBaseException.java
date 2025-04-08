package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class ValidationBaseException extends BankAppBaseException {
    /**
     * Constructs a new ValidationBaseException with the specified detail message, cause, and error code.
     *
     * @param message the detail message explaining the validation error
     * @param cause the underlying cause of the exception, or null if none is specified
     * @param errorCode the error code representing the specific validation error
     */
    public ValidationBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    /**
     * Constructs a new ValidationBaseException with the specified detail message and error code.
     *
     * @param message the detail message describing the validation error
     * @param errorCode the error code identifying the type of validation error encountered
     */
    public ValidationBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
