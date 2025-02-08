package info.mackiewicz.bankapp.exception;

public class TransactionDestinationAccountNotSpecifiedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Transaction destination account must be specified";

    public TransactionDestinationAccountNotSpecifiedException() {
        super(DEFAULT_MESSAGE);
    }

    public TransactionDestinationAccountNotSpecifiedException(String message) {
        super(message);
    }
}
