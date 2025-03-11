package info.mackiewicz.bankapp.shared.exception;

public class InvalidTransactionTypeException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Invalid transaction type";
        public InvalidTransactionTypeException() {
            super(DEFAULT_MESSAGE);
    }

}
