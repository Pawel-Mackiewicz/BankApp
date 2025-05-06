package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ApiExceptionHandler;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

/**
 * Base exception class for the BankApp application's exception hierarchy.
 *
 * <p>This class extends RuntimeException and includes an error code to provide
 * standardized error handling across the application. All application-specific
 * exceptions should extend this class.</p>
 *
 * <p>The included error code maps to specific HTTP status codes and user-friendly
 * messages, enabling consistent API error responses.</p>
 *
 * @see ErrorCode
 * @see ApiExceptionHandler
 */
public class BankAppBaseException extends RuntimeException {
    
    private final ErrorCode errorCode;

    /**
     * Constructs a new BankAppBaseException with a message and error code.
     *
     * @param message detailed error message
     * @param errorCode specific error code that categorizes this exception
     */
    public BankAppBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BankAppBaseException with a message, cause, and error code.
     *
     * @param message detailed error message
     * @param cause the underlying cause of this exception
     * @param errorCode specific error code that categorizes this exception
     */
    public BankAppBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code that categorizes this exception
     * @see ErrorCode
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
