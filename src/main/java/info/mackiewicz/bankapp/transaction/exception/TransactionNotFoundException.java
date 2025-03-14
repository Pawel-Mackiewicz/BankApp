package info.mackiewicz.bankapp.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction not found";

    public TransactionNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
