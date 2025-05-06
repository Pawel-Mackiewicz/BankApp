package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class UnsupportedValidationTypeException extends ValidationBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    public UnsupportedValidationTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    public UnsupportedValidationTypeException(String message) {
        super(message, ERROR_CODE);
    }

}
