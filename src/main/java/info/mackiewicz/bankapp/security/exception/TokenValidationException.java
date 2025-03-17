package info.mackiewicz.bankapp.security.exception;

public class TokenValidationException extends InvalidPasswordResetTokenException {
    
    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
