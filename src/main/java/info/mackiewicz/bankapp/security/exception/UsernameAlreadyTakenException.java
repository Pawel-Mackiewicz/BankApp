package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UsernameAlreadyTakenException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USERNAME_TAKEN;

    public UsernameAlreadyTakenException(String message) {
        super(message, ERROR_CODE);
    }

    public UsernameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
