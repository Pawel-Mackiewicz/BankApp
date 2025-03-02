package info.mackiewicz.bankapp.shared.exception;

public class TransactionCannotBeProcessedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction cannot be processed";

    public TransactionCannotBeProcessedException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionCannotBeProcessedException(String message) {
        super(message);
    }
}
