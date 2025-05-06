package info.mackiewicz.bankapp.core.user.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class InvalidEmailFormatException extends UserBaseException {
    private static final String DEFAULT_MESSAGE = "Invalid email format";

    public InvalidEmailFormatException() {
        super(DEFAULT_MESSAGE, ErrorCode.VALIDATION_ERROR);
    }

    public InvalidEmailFormatException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

}
