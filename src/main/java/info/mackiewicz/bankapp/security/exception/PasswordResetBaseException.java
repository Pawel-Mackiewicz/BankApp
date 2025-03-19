package info.mackiewicz.bankapp.security.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public abstract class PasswordResetBaseException extends BankAppBaseException {

    protected PasswordResetBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    protected PasswordResetBaseException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause, errorCode);
    }
}
