package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class InvalidPasswordException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INVALID_PASSWORD;
    public InvalidPasswordException(String message) {
        super(message, ERROR_CODE);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
