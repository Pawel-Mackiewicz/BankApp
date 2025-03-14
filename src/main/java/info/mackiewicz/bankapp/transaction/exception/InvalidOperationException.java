package info.mackiewicz.bankapp.transaction.exception;

/**
 * Exception thrown when an financial operation is not allowed.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}