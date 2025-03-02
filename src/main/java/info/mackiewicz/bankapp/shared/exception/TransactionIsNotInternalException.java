package info.mackiewicz.bankapp.shared.exception;

public class TransactionIsNotInternalException extends RuntimeException {
    public TransactionIsNotInternalException(String message) {
        super(message);
    }
    
    public TransactionIsNotInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
