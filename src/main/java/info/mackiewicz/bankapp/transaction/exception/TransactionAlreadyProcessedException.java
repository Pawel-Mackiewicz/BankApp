package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class TransactionAlreadyProcessedException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "This transaction has already been processed.";

    public TransactionAlreadyProcessedException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_ALREADY_PROCESSED);
    }

    public TransactionAlreadyProcessedException(String message) {
        super(message, ErrorCode.TRANSACTION_ALREADY_PROCESSED);
    }
}
