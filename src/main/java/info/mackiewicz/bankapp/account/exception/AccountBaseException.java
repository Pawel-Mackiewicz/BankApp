package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public abstract class AccountBaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public AccountBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AccountBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
