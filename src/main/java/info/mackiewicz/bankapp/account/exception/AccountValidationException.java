package info.mackiewicz.bankapp.account.exception;

public class AccountValidationException extends RuntimeException {
    final static String MESSAGE = "Account validation failed";
    public AccountValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountValidationException(String message) {
        super(message);
    }

    public AccountValidationException() {
        super(MESSAGE);
    }

}
