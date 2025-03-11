package info.mackiewicz.bankapp.transaction.exception;

public class InvalidTransactionTypeException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Invalid transaction type";
        public InvalidTransactionTypeException() {
            super(DEFAULT_MESSAGE);
    }

}
