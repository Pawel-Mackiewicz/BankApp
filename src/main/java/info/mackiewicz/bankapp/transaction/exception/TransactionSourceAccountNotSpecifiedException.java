package info.mackiewicz.bankapp.transaction.exception;

public class TransactionSourceAccountNotSpecifiedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction source account must be specified";

    public TransactionSourceAccountNotSpecifiedException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionSourceAccountNotSpecifiedException(String message) {
        super(message);
    }
}
