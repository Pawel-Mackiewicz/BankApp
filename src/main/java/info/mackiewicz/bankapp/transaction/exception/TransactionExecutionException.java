package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

/**
 * Exception thrown when transaction execution fails
 */
public class TransactionExecutionException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Unable to process transaction. Please try again later.";

    public TransactionExecutionException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_EXECUTION_ERROR);
    }

    public TransactionExecutionException(String message) {
        super(message, ErrorCode.TRANSACTION_EXECUTION_ERROR);
    }

    public TransactionExecutionException(String message, Throwable cause) {
        super(message, cause, ErrorCode.TRANSACTION_EXECUTION_ERROR);
    }
}