package info.mackiewicz.bankapp.core.user.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class InvalidPeselFormatException extends UserBaseException {
    private static final String DEFAULT_MESSAGE = "Invalid PESEL format";

    public InvalidPeselFormatException() {
        super(DEFAULT_MESSAGE, ErrorCode.VALIDATION_ERROR);
    }

    public InvalidPeselFormatException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

}
