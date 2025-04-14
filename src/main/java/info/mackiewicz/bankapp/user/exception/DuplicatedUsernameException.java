package info.mackiewicz.bankapp.user.exception;

public class DuplicatedUsernameException extends DuplicatedUserException {
    public DuplicatedUsernameException(String message) {
        super(message);
    }
    public DuplicatedUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
