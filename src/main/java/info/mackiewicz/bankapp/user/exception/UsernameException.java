package info.mackiewicz.bankapp.user.exception;

import info.mackiewicz.bankapp.system.error.handling.core.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class UsernameException extends BankAppBaseException {
    private static final ErrorCode ERROR_CODE = ErrorCode.VALIDATION_ERROR;

    public UsernameException(String message) {
        super(message, ERROR_CODE);
    }

    public UsernameException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
