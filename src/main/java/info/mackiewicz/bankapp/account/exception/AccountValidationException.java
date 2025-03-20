package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class AccountValidationException extends AccountBaseException {

    public AccountValidationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.VALIDATION_ERROR);
    }

    public AccountValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}
