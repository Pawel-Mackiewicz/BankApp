package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class ValidationBaseException extends BankAppBaseException {
    /**
     * Constructs a new ValidationBaseException with the specified detail message, cause, and error code.
     *
     * @param message the detail message explaining the validation error
     * @param cause the underlying reason for the exception
     * @param errorCode the specific error code associated with this validation error
     */
    public ValidationBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    /**
     * Constructs a new ValidationBaseException with the specified error message and error code.
     *
     * @param message the error message describing the validation failure
     * @param errorCode the error code associated with the validation issue
     */
    public ValidationBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
