package info.mackiewicz.bankapp.shared.exception;

public class OwnerAccountsNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "No accounts found for the given owner";

    public OwnerAccountsNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public OwnerAccountsNotFoundException(String message) {
        super(message);
    }
}
