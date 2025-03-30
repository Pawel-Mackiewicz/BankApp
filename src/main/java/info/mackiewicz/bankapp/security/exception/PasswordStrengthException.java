package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class PasswordStrengthException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.PASSWORD_TOO_WEAK;

    public PasswordStrengthException(String message) {
        super(message, ERROR_CODE);
    }

    public PasswordStrengthException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
