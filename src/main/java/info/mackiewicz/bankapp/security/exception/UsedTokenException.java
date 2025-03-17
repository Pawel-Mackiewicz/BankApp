package info.mackiewicz.bankapp.security.exception;

public class UsedTokenException extends InvalidTokenException {
    
    public UsedTokenException(String message) {
        super(message);
    }

    public UsedTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}
