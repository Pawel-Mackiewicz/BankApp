package info.mackiewicz.bankapp.transaction.exception;

/**
 * Exception thrown when transaction execution fails
 */
public class TransactionExecutionException extends RuntimeException {
    
    public TransactionExecutionException() {
        super();
    }
    public TransactionExecutionException(String message) {
        super(message);
    }

    public TransactionExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}