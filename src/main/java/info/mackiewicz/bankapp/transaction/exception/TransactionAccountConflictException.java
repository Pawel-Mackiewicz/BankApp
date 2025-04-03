package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when transaction validation fails
 */
public class TransactionAccountConflictException extends TransactionBaseException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_ACCOUNT_CONFLICT;
    
    public TransactionAccountConflictException(String message) {
        super(message, ERROR_CODE);
    }

    public TransactionAccountConflictException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}