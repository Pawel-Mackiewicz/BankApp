package info.mackiewicz.bankapp.presentation.auth.recovery.password.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class TokenException extends PasswordResetBaseException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TOKEN_INVALID;
    
    public TokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TokenException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public TokenException(String message) {
        super(message, ERROR_CODE);
    }

    public TokenException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
}