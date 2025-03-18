package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;

public class PasswordChangeException extends PasswordResetException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;
    
    public PasswordChangeException(String message) {
        super(message, ERROR_CODE);
    }

    public PasswordChangeException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

}
