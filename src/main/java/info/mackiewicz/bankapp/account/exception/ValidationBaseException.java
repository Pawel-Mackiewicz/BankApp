package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class ValidationBaseException extends BankAppBaseException {
    /**
     * Constructs a new ValidationBaseException with the specified detail message, underlying cause, and error code.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception
     * @param errorCode the error code representing the type of validation error
     */
    public ValidationBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    /**
     * Constructs a new ValidationBaseException with the specified error message
     * and error code.
     *
     * @param message the detail message for this exception
     * @param errorCode the error code representing the specific validation error
     */
    public ValidationBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
