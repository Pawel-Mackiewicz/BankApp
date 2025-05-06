package info.mackiewicz.bankapp.system.recovery.password.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class UsedTokenException extends TokenException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TOKEN_USED;
    
    public UsedTokenException(String message) {
        super(message, ERROR_CODE);
    }

    public UsedTokenException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

}
