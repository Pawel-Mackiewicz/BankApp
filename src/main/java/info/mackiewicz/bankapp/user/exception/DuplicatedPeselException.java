package info.mackiewicz.bankapp.user.exception;

public class DuplicatedPeselException extends DuplicatedUserException {
    public DuplicatedPeselException(String message) {
        super(message);
    }
    public DuplicatedPeselException(String message, Throwable cause) {
        super(message, cause);
    }
}
