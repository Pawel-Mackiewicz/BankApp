package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class TransactionSourceAccountNotSpecifiedException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Source account is required for this transaction.";

    public TransactionSourceAccountNotSpecifiedException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_SOURCE_ACCOUNT_MISSING);
    }

    public TransactionSourceAccountNotSpecifiedException(String message) {
        super(message, ErrorCode.TRANSACTION_SOURCE_ACCOUNT_MISSING);
    }
}
