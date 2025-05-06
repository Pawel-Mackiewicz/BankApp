package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class AccountValidationException extends AccountBaseException {

    public AccountValidationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.VALIDATION_ERROR);
    }

    public AccountValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}
