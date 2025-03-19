package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public class AccountOwnerLockedException extends AccountBaseException {

    public AccountOwnerLockedException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_OWNER_LOCKED);
    }

    public AccountOwnerLockedException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_LOCKED);
    }
}
