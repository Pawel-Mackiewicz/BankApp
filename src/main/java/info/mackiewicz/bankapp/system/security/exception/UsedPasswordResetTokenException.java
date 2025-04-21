package info.mackiewicz.bankapp.system.security.exception;

public class UsedPasswordResetTokenException extends InvalidPasswordResetTokenException {
    
    public UsedPasswordResetTokenException(String message) {
        super(message);
    }

    public UsedPasswordResetTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
