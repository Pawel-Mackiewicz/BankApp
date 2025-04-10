package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class AccountOwnershipException extends AccountBaseException {
    private static final ErrorCode ERROR_CODE = ErrorCode.ACCOUNT_OWNERSHIP_ERROR;

    public AccountOwnershipException(String message) {
        super(message, ERROR_CODE);
    }

    public AccountOwnershipException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
