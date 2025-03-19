package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public class AccountLimitException extends AccountBaseException {

    public AccountLimitException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_LIMIT_EXCEEDED);
    }

    public AccountLimitException(String message) {
        super(message, ErrorCode.ACCOUNT_LIMIT_EXCEEDED);
    }

}
