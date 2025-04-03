package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class TransactionValidationException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_VALIDATION_ERROR;

    public TransactionValidationException(String message) {
        super(message, ERROR_CODE);
    }

    
    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
