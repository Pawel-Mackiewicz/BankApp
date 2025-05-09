package info.mackiewicz.bankapp.presentation.auth.recovery.password.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public abstract class PasswordResetBaseException extends BankAppBaseException {

    protected PasswordResetBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    protected PasswordResetBaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause, errorCode);
    }
}
