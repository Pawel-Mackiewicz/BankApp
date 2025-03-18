package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;

public abstract class PasswordResetException extends RuntimeException {

    private final ErrorCode errorCode;

    protected PasswordResetException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected PasswordResetException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
