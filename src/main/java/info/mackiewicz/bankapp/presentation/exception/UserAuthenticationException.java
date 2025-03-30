package info.mackiewicz.bankapp.presentation.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UserAuthenticationException extends PresentationBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.AUTHENTICATION_ERROR;

    public UserAuthenticationException(String message) {
        super(message, ERROR_CODE);
    }

    public UserAuthenticationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
