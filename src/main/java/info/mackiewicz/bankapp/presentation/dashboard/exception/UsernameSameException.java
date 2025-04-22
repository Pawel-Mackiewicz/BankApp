package info.mackiewicz.bankapp.presentation.dashboard.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class UsernameSameException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USERNAME_SAME;

    public UsernameSameException(String message) {
        super(message, ERROR_CODE);
    }

    public UsernameSameException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
