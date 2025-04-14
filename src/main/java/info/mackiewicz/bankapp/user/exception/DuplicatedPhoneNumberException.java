package info.mackiewicz.bankapp.user.exception;

public class DuplicatedPhoneNumberException extends DuplicatedUserException {
    public DuplicatedPhoneNumberException(String message) {
        super(message);
    }
    public DuplicatedPhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
