package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class ValidationBaseException extends BankAppBaseException {
    public ValidationBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ValidationBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
