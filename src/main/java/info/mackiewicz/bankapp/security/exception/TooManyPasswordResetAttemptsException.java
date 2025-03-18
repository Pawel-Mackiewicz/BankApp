package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;

/**
 * Exception thrown when a user has too many active password reset tokens
 */
public class TooManyPasswordResetAttemptsException extends PasswordResetException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS;
    public TooManyPasswordResetAttemptsException(String message) {
        super(message, ERROR_CODE);
    }
    public TooManyPasswordResetAttemptsException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
}