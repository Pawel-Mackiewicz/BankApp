package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class AccountOwnerExpiredException extends AccountBaseException {
    final static String MESSAGE = "Account owner is expired";

    public AccountOwnerExpiredException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_OWNER_EXPIRED);
    }

    public AccountOwnerExpiredException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_EXPIRED);
    }
}
