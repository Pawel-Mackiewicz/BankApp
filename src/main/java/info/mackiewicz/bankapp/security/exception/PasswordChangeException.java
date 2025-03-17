package info.mackiewicz.bankapp.security.exception;

public class PasswordChangeException extends PasswordResetException {

    public PasswordChangeException(String message) {
        super(message);
    }

    public PasswordChangeException(String message, Throwable cause) {
        super(message, cause);
    }

}
