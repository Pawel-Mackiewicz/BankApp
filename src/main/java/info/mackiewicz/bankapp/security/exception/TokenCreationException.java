package info.mackiewicz.bankapp.security.exception;

public class TokenCreationException extends RuntimeException {

    
    public TokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenCreationException(String message) {
        super(message);
    }

}
