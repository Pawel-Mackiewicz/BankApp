package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class IbanAnalysisException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    
    public IbanAnalysisException(String message) {
        super(message, ERROR_CODE);
    }

    public IbanAnalysisException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }


    

}
