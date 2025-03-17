package info.mackiewicz.bankapp.security.exception;

public class TokenValidationException extends InvalidTokenException {
    
    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
