package info.mackiewicz.bankapp.presentation.dashboard.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class ForbiddenUsernameException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USERNAME_FORBIDDEN;

    public ForbiddenUsernameException(String message) {
        super(message, ERROR_CODE);
    }
    public ForbiddenUsernameException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
