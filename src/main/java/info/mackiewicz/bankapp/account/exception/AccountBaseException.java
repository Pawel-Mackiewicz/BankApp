package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public abstract class AccountBaseException extends BankAppBaseException {

    public AccountBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AccountBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
