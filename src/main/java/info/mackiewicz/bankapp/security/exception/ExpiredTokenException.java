package info.mackiewicz.bankapp.security.exception;

public class ExpiredTokenException extends InvalidTokenException {
    
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
