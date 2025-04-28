package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class TransactionAmountNotSpecifiedException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Transaction amount is required.";

    public TransactionAmountNotSpecifiedException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_AMOUNT_MISSING);
    }

    public TransactionAmountNotSpecifiedException(String message) {
        super(message, ErrorCode.TRANSACTION_AMOUNT_MISSING);
    }
}
