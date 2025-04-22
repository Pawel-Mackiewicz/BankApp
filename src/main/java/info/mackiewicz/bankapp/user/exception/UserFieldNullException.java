package info.mackiewicz.bankapp.user.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class UserFieldNullException extends UserBaseException {
    private static final ErrorCode ERROR_CODE = ErrorCode.VALIDATION_ERROR;

    public UserFieldNullException(String message) {
        super(message, ERROR_CODE);
    }
}
