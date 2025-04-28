package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class AccountOwnerLockedException extends AccountBaseException {

    public AccountOwnerLockedException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_OWNER_LOCKED);
    }

    public AccountOwnerLockedException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_LOCKED);
    }
}
