package info.mackiewicz.bankapp.system.banking.history.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when transaction filtering fails
 */
public class TransactionFilterException extends TransactionHistoryBaseException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;
    
    public TransactionFilterException(String message) {
        super(message, ERROR_CODE);
    }

    public TransactionFilterException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}