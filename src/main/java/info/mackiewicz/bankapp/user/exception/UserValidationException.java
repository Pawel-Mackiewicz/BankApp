package info.mackiewicz.bankapp.user.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public class UserValidationException extends UserBaseException {
    public UserValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

    public UserValidationException(String message, Throwable cause) {
        super(message, cause, ErrorCode.VALIDATION_ERROR);
    }
}
