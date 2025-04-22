package info.mackiewicz.bankapp.shared.exception;

import info.mackiewicz.bankapp.system.error.handling.core.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class InvalidSourceTypeException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    public InvalidSourceTypeException(String message) {
        super(message, ERROR_CODE);
    }
    public InvalidSourceTypeException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
