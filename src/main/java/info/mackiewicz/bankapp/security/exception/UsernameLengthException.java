package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UsernameLengthException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USERNAME_LENGTH;

    public UsernameLengthException(String message) {
        super(message, ERROR_CODE);
    }

    public UsernameLengthException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
