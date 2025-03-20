package info.mackiewicz.bankapp.security.exception;

public class ExpiredPasswordResetTokenException extends InvalidPasswordResetTokenException {
    
    public ExpiredPasswordResetTokenException(String message) {
        super(message);
    }

    public ExpiredPasswordResetTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
