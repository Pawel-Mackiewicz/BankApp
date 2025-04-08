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
     * <p>This exception is thrown when an IBAN string fails validation and cannot be converted
     * into an Iban object. The error code is pre-set to indicate an invalid IBAN.</p>
     *
     * @param message the detail message describing the reason for the exception
     */
    public InvalidIbanException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new InvalidIbanException with the specified detail message and cause.
     * This exception signals that an IBAN string conversion failed validation and is considered invalid.
     * The error code is automatically set to {@code ErrorCode.INVALID_IBAN}.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception
     */
    public InvalidIbanException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}