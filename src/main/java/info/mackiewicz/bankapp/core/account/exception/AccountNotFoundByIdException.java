package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class AccountNotFoundByIdException extends AccountBaseException {

    public AccountNotFoundByIdException(String message) {
        super(message, ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
