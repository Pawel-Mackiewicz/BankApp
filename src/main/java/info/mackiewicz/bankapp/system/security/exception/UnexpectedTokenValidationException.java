package info.mackiewicz.bankapp.system.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UnexpectedTokenValidationException extends TokenException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;
    public UnexpectedTokenValidationException(String message) {
        super(message, ERROR_CODE);
    }

    public UnexpectedTokenValidationException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }

}
