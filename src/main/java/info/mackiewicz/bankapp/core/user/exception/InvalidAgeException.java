package info.mackiewicz.bankapp.core.user.exception;

/**
 * Exception thrown when a user's age does not meet the required criteria.
 * Users must be at least 18 years old and not older than 120 years.
 */
public class InvalidAgeException extends UserValidationException {
    
    public InvalidAgeException(String message) {
        super(message);
    }
}
