package info.mackiewicz.bankapp.system.security.exception;

public class InvalidPasswordResetTokenException extends RuntimeException {
    
    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }

    public InvalidPasswordResetTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}