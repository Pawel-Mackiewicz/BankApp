package info.mackiewicz.bankapp.security.exception;

/**
 * Exception thrown when a user has too many active password reset tokens
 */
public class TooManyPasswordResetAttemptsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Too many password reset attempts detected. Please check your email inbox.";


    public TooManyPasswordResetAttemptsException() {
        super(DEFAULT_MESSAGE);
    }
    public TooManyPasswordResetAttemptsException(String message) {
        super(message);
    }
}