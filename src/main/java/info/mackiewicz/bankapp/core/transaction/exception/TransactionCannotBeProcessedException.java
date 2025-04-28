package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class TransactionCannotBeProcessedException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Transaction cannot be processed in its current state.";

    public TransactionCannotBeProcessedException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_CANNOT_BE_PROCESSED);
    }

    public TransactionCannotBeProcessedException(String message) {
        super(message, ErrorCode.TRANSACTION_CANNOT_BE_PROCESSED);
    }
}
