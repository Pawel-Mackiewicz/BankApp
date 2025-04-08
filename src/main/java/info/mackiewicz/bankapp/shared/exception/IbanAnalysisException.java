package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class IbanAnalysisException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    
    /**
     * Constructs a new IbanAnalysisException with the specified detail message.
     *
     * <p>The error code for this exception is fixed as {@code ErrorCode.INTERNAL_ERROR}.
     *
     * @param message the detail message explaining the error
     */
    public IbanAnalysisException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new IbanAnalysisException with the specified detail message and cause.
     *
     * <p>This exception uses a predefined error code to indicate an internal error related to IBAN analysis.</p>
     *
     * @param message the detailed error message
     * @param cause the underlying cause of the exception, or null if not applicable
     */
    public IbanAnalysisException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }


    

}
