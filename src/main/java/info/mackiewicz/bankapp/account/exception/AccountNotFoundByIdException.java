package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public class AccountNotFoundByIdException extends AccountBaseException {

    public AccountNotFoundByIdException(String message) {
        super(message, ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
