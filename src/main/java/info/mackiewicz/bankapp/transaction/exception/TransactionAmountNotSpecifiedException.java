package info.mackiewicz.bankapp.transaction.exception;

public class TransactionAmountNotSpecifiedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction amount must be specified";

    public TransactionAmountNotSpecifiedException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionAmountNotSpecifiedException(String message) {
        super(message);
    }
}
