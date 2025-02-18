package info.mackiewicz.bankapp.exception;

public class TransactionIsNotInternalException extends RuntimeException {
    public TransactionIsNotInternalException(String message) {
        super(message);
    }
    
    public TransactionIsNotInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
