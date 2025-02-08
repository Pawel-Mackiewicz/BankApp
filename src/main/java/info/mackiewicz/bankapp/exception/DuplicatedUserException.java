package info.mackiewicz.bankapp.exception;

public class DuplicatedUserException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User with the same PESEL already exists";

    public DuplicatedUserException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicatedUserException(String message) {
        super(message);
    }
}
