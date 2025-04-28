package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

/**
 * Exception thrown when an account does not have sufficient funds to complete a transaction.
 */
public class InsufficientFundsException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Insufficient funds for this transaction. Please check your balance and try again.";

    public InsufficientFundsException() {
        super(DEFAULT_MESSAGE, ErrorCode.INSUFFICIENT_FUNDS);
    }

    public InsufficientFundsException(String message) {
        super(message, ErrorCode.INSUFFICIENT_FUNDS);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause, ErrorCode.INSUFFICIENT_FUNDS);
    }
}