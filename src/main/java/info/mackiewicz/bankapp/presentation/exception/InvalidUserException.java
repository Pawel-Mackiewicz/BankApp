package info.mackiewicz.bankapp.presentation.exception;

public class InvalidUserException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User ID must not be null for update.";

    public InvalidUserException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidUserException(String message) {
        super(message);
    }
}
