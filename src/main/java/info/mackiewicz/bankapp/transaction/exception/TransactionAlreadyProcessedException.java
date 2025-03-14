package info.mackiewicz.bankapp.transaction.exception;

public class TransactionAlreadyProcessedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction has already been processed";

    public TransactionAlreadyProcessedException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionAlreadyProcessedException(String message) {
        super(message);
    }
}
