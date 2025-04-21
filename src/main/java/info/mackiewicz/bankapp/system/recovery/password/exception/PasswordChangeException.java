package info.mackiewicz.bankapp.system.recovery.password.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class PasswordChangeException extends PasswordResetBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;
    
    public PasswordChangeException(String message) {
        super(message, ERROR_CODE);
    }

    public PasswordChangeException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

}
