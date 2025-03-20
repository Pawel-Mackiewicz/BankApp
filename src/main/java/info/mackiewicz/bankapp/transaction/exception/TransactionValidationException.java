package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when transaction validation fails
 */
public class TransactionValidationException extends TransactionBaseException {
    
    private static final String DEFAULT_MESSAGE = "Transaction validation failed. Please check your transaction details.";
    
    public TransactionValidationException() {
        super(DEFAULT_MESSAGE, ErrorCode.TRANSACTION_VALIDATION_ERROR);
    }
    
    public TransactionValidationException(String message) {
        super(message, ErrorCode.TRANSACTION_VALIDATION_ERROR);
    }

    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.TRANSACTION_VALIDATION_ERROR);
    }
}