package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class TransactionNotFoundException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Transaction not found";

    public TransactionNotFoundException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_NOT_FOUND);
    }

    public TransactionNotFoundException(String message) {
        super(message, ErrorCode.TRANSACTION_NOT_FOUND);
    }
}
