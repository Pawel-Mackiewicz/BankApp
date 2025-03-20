package info.mackiewicz.bankapp.user.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class InvalidPeselFormatException extends UserBaseException {
    private static final String DEFAULT_MESSAGE = "Invalid PESEL format";

    public InvalidPeselFormatException() {
        super(DEFAULT_MESSAGE, ErrorCode.VALIDATION_ERROR);
    }

    public InvalidPeselFormatException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }

}
