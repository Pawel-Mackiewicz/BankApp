package info.mackiewicz.bankapp.transaction.exception;

public class TransactionTypeNotSpecifiedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction type must be specified";

    public TransactionTypeNotSpecifiedException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionTypeNotSpecifiedException(String message) {
        super(message);
    }
}
