package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class IbanAnalysisException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    
    /**
     * Constructs a new IbanAnalysisException with the specified detail message.
     * This exception encapsulates errors related to IBAN analysis and is initialized
     * with a predefined error code indicating an internal error.
     *
     * @param message the detail message describing the exception
     */
    public IbanAnalysisException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new IbanAnalysisException with the specified detail message and cause.
     * The error code is set to ErrorCode.INTERNAL_ERROR.
     *
     * @param message the detail message explaining the reason for this exception
     * @param cause the underlying exception that caused this error
     */
    public IbanAnalysisException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }


    

}
