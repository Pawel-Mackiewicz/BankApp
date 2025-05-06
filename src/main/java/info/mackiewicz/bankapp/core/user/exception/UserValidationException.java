package info.mackiewicz.bankapp.core.user.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class UserValidationException extends UserBaseException {
    public UserValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

    public UserValidationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.VALIDATION_ERROR);
    }
}
