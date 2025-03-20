package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class AccountOwnerNullException extends AccountBaseException {

    public AccountOwnerNullException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_OWNER_NULL);
    }

    public AccountOwnerNullException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_NULL);
    }
}
