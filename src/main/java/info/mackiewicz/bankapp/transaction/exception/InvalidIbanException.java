package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when IBAN string cannot be converted to an Iban object.
 * This exception is used by IbanConverter when IBAN validation fails.
 */
public class InvalidIbanException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INVALID_IBAN;

    /**
     * Constructs a new InvalidIbanException with the specified detail message.
     *
     * @param message the detail message explaining why the IBAN is invalid
     */
    public InvalidIbanException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new InvalidIbanException with the specified detail message and cause.
     *
     * <p>This exception is thrown when an IBAN string cannot be converted into an Iban object,
     * indicating an invalid IBAN error. The predefined error code for invalid IBAN situations is used.
     *
     * @param message the detail message explaining the exception
     * @param cause the underlying cause of the exception
     */
    public InvalidIbanException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}