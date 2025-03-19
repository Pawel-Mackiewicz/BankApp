package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public class OwnerAccountsNotFoundException extends AccountBaseException {

    public OwnerAccountsNotFoundException(String message) {
        super(message, ErrorCode.ACCOUNT_OWNER_NOT_FOUND);
    }
}
