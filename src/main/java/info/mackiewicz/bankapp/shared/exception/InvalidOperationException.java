package info.mackiewicz.bankapp.shared.exception;

/**
 * Wyjątek rzucany gdy operacja finansowa jest nieprawidłowa.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}