package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class AccountLimitException extends AccountBaseException {

    public AccountLimitException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_LIMIT_EXCEEDED);
    }

    public AccountLimitException(String message) {
        super(message, ErrorCode.ACCOUNT_LIMIT_EXCEEDED);
    }

}
