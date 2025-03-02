package info.mackiewicz.bankapp.shared.exception;

public class AccountNotFoundByIdException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Account not found for the provided ID";

    public AccountNotFoundByIdException() {
        super(DEFAULT_MESSAGE);
    }

    public AccountNotFoundByIdException(String message) {
        super(message);
    }
}
