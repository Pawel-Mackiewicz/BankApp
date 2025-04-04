package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class PasswordSameException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.PASSWORD_SAME;

    public PasswordSameException(String message) {
        super(message, ERROR_CODE);
    }

    public PasswordSameException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
