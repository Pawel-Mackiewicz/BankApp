package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

/**
 * Exception thrown when IBAN string cannot be converted to an Iban object.
 * This exception is used by IbanConverter when IBAN validation fails.
 */
public class InvalidIbanException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INVALID_IBAN;

    public InvalidIbanException(String message) {
        super(message, ERROR_CODE);
    }

    public InvalidIbanException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}