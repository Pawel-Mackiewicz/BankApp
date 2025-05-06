package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class TransactionDestinationAccountNotSpecifiedException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "Destination account is required for this transaction.";

    public TransactionDestinationAccountNotSpecifiedException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_DESTINATION_ACCOUNT_MISSING);
    }

    public TransactionDestinationAccountNotSpecifiedException(String message) {
        super(message, ErrorCode.TRANSACTION_DESTINATION_ACCOUNT_MISSING);
    }
}
