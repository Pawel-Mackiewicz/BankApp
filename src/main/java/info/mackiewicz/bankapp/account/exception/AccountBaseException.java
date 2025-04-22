package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public abstract class AccountBaseException extends BankAppBaseException {

    public AccountBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AccountBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
