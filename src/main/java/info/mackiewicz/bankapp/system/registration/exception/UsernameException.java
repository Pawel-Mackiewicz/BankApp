package info.mackiewicz.bankapp.system.registration.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UsernameException extends BankAppBaseException {
    private static final ErrorCode ERROR_CODE = ErrorCode.VALIDATION_ERROR;

    public UsernameException(String message) {
        super(message, ERROR_CODE);
    }

    public UsernameException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
