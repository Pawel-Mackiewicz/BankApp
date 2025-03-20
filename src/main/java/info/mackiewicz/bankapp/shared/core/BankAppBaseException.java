package info.mackiewicz.bankapp.shared.core;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Base exception class for the BankApp application.
 * This class extends RuntimeException and includes an error code.
 */
public class BankAppBaseException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public BankAppBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BankAppBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
