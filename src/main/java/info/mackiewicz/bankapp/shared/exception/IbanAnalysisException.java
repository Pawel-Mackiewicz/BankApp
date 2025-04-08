package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class IbanAnalysisException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    
    /**
     * Constructs a new IbanAnalysisException with the specified detail message.
     *
     * @param message the detail message describing the IBAN analysis error.
     */
    public IbanAnalysisException(String message) {
        super(message, ERROR_CODE);
    }

    /**
     * Constructs a new IbanAnalysisException with the specified detail message and cause.
     *
     * <p>This exception is used to signal errors that occur during IBAN analysis and automatically
     * assigns a predefined internal error code.
     *
     * @param message the detail message to explain the exception
     * @param cause the underlying cause of the exception
     */
    public IbanAnalysisException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }


    

}
