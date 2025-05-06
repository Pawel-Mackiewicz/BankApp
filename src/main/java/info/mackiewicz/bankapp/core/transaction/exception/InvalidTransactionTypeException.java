package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class InvalidTransactionTypeException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Invalid transaction type.";

    public InvalidTransactionTypeException() {
        super(DEFAULT_MESSAGE, ErrorCode.INVALID_TRANSACTION_TYPE);
    }

    public InvalidTransactionTypeException(String message) {
        super(message, ErrorCode.INVALID_TRANSACTION_TYPE);
    }
}
