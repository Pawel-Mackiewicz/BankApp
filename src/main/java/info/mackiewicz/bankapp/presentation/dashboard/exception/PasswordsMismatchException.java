package info.mackiewicz.bankapp.presentation.dashboard.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class PasswordsMismatchException extends CredentialsBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.PASSWORD_MISMATCH;

    public PasswordsMismatchException(String message) {
        super(message, ERROR_CODE);
    }

    public PasswordsMismatchException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
