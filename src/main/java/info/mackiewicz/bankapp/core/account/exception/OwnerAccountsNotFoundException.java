package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class OwnerAccountsNotFoundException extends AccountBaseException {

    public OwnerAccountsNotFoundException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_NOT_FOUND);
    }
}
