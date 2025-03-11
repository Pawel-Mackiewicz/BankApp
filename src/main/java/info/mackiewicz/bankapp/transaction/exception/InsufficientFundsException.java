package info.mackiewicz.bankapp.transaction.exception;

/**
 * Exception thrown when there are insufficient funds in the account to complete the transaction
 */
public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}