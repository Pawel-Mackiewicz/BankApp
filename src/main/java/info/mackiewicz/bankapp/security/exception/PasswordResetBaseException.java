package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public abstract class PasswordResetBaseException extends RuntimeException {

    private final ErrorCode errorCode;

    protected PasswordResetBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected PasswordResetBaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
