package info.mackiewicz.bankapp.account.exception;

public class AccountNotFoundByIbanException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Account not found for the provided IBAN";

    public AccountNotFoundByIbanException() {
        super(DEFAULT_MESSAGE);
    }
    public AccountNotFoundByIbanException(String message) {
        super(message);
    }
    
    public AccountNotFoundByIbanException(String iban, Throwable cause) {
        super("Account not found for IBAN: " + iban, cause);
    }

}
