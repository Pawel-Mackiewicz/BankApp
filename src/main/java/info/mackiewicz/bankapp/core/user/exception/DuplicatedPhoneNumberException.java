package info.mackiewicz.bankapp.core.user.exception;

public class DuplicatedPhoneNumberException extends DuplicatedUserException {
    public DuplicatedPhoneNumberException(String message) {
        super(message);
    }
    public DuplicatedPhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
