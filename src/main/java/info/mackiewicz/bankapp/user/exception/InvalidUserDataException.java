package info.mackiewicz.bankapp.user.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Thrown to indicate that user data provided is invalid and cannot be processed.<br>
 * This exception is intended to signal an internal error when user data validation fails
 * at a critical point of the application logic.
 * <p>
 * See {@link ErrorCode} for more details about the associated error code.
 */
public class InvalidUserDataException extends UserBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    public InvalidUserDataException(String message) {
        super(message, ERROR_CODE);
    }

    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
