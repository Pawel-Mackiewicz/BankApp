package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class AccountOwnerNullException extends AccountBaseException {

    public AccountOwnerNullException(String message, Throwable cause) {
        super(message, cause, ErrorCode.ACCOUNT_OWNER_NULL);
    }

    public AccountOwnerNullException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_NULL);
    }
}
