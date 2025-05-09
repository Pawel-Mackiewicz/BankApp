package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class AccountNotFoundByIbanException extends AccountBaseException {


    public AccountNotFoundByIbanException(String message) {
        super(message, ErrorCode.ACCOUNT_NOT_FOUND);
    }
    
    public AccountNotFoundByIbanException(String iban, Throwable cause) {
        super("Account not found for IBAN: " + iban, cause, ErrorCode.ACCOUNT_NOT_FOUND);
    }

}
