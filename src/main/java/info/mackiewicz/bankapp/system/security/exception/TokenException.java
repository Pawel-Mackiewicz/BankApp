package info.mackiewicz.bankapp.system.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

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