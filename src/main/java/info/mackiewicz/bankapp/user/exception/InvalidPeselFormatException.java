package info.mackiewicz.bankapp.user.exception;

public class InvalidPeselFormatException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid PESEL format";

    public InvalidPeselFormatException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidPeselFormatException(String message) {
        super(message);
    }

}
