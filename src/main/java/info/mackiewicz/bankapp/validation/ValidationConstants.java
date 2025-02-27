package info.mackiewicz.bankapp.validation;

public final class ValidationConstants {
    
    private ValidationConstants() {
        // Prevent instantiation
    }

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&]).*$";
    public static final String PASSWORD_DESCRIPTION = "Password must contain at least one digit, one lowercase, one uppercase letter and one special character (@$!%*?&)";
    public static final int PASSWORD_MIN_LENGTH = 8;
}
