package info.mackiewicz.bankapp.presentation.auth.recovery.password.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class ExpiredTokenException extends TokenException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TOKEN_EXPIRED;
    public ExpiredTokenException(String message) {
        super(message, ERROR_CODE);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

}
