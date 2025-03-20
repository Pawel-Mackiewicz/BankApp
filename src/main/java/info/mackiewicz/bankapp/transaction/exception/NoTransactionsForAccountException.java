package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when no transactions are found for a given account.
 */
public class NoTransactionsForAccountException extends TransactionBaseException {
    private static final String DEFAULT_MESSAGE = "No transactions found for this account.";

    public NoTransactionsForAccountException() {
        super(DEFAULT_MESSAGE, ErrorCode.NO_TRANSACTIONS_FOR_ACCOUNT);
    }

    public NoTransactionsForAccountException(String message) {
        super(message, ErrorCode.NO_TRANSACTIONS_FOR_ACCOUNT);
    }

    public NoTransactionsForAccountException(String message, Throwable cause) {
        super(message, cause, ErrorCode.NO_TRANSACTIONS_FOR_ACCOUNT);
    }
}
