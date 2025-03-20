package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when an financial operation is not allowed.
 */
public class InvalidOperationException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Invalid operation for this transaction.";

    public InvalidOperationException() {
        super(DEFAULT_MESSAGE, ErrorCode.INVALID_TRANSACTION_OPERATION);
    }

    public InvalidOperationException(String message) {
        super(message, ErrorCode.INVALID_TRANSACTION_OPERATION);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.INVALID_TRANSACTION_OPERATION);
    }
}