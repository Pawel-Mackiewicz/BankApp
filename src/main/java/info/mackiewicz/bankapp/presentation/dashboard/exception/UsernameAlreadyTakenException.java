package info.mackiewicz.bankapp.presentation.dashboard.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class UsernameAlreadyTakenException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USERNAME_TAKEN;

    public UsernameAlreadyTakenException(String message) {
        super(message, ERROR_CODE);
    }

    public UsernameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
