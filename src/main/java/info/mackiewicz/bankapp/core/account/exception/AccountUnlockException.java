package info.mackiewicz.bankapp.core.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

public class AccountUnlockException extends AccountBaseException {
    private final Integer accountId;

    public AccountUnlockException(String message, Integer accountId) {
        super(message, ErrorCode.INTERNAL_ERROR);
        this.accountId = accountId;
    }

    public Integer getAccountId() { return accountId; }
}