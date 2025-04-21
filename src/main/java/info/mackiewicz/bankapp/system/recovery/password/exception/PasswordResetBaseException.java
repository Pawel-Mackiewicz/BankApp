package info.mackiewicz.bankapp.system.recovery.password.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public abstract class PasswordResetBaseException extends BankAppBaseException {

    protected PasswordResetBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    protected PasswordResetBaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause, errorCode);
    }
}
