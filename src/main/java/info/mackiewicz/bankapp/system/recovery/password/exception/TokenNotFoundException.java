package info.mackiewicz.bankapp.system.recovery.password.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TokenNotFoundException extends TokenException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TOKEN_NOT_FOUND;
    public TokenNotFoundException(String message) {
        super(message, ERROR_CODE);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
}
