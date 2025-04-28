package info.mackiewicz.bankapp.core.user.exception;

public class DuplicatedEmailException extends DuplicatedUserException {
    public DuplicatedEmailException(String message) {
        super(message);
    }
    public DuplicatedEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
