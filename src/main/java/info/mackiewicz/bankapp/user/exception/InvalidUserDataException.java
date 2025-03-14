package info.mackiewicz.bankapp.user.exception;

public class InvalidUserDataException extends IllegalArgumentException {

    public InvalidUserDataException(String message) {
        super(message);
    }
}
