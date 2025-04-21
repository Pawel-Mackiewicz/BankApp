package info.mackiewicz.bankapp.system.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UsedTokenException extends TokenException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TOKEN_USED;
    
    public UsedTokenException(String message) {
        super(message, ERROR_CODE);
    }

    public UsedTokenException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

}
