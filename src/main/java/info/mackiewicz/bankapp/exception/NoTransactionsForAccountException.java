package info.mackiewicz.bankapp.exception;

public class NoTransactionsForAccountException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "No transactions found for the given account";

    public NoTransactionsForAccountException() {
        super(DEFAULT_MESSAGE);
    }

    public NoTransactionsForAccountException(String message) {
        super(message);
    }
}
