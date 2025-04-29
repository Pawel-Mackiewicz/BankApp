package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class AccountDeletionException extends AccountBaseException {
    private static final ErrorCode ERROR_CODE = ErrorCode.ACCOUNT_DELETION_FORBIDDEN;

    public AccountDeletionException(String message) {
        super(message, ERROR_CODE);
    }

    public AccountDeletionException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
