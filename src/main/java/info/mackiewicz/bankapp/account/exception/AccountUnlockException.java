package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.shared.exception.handler.ErrorCode;

public class AccountUnlockException extends AccountBaseException {
    private final Integer accountId;

    public AccountUnlockException(String message, Integer accountId) {
        super(message, ErrorCode.INTERNAL_ERROR);
        this.accountId = accountId;
    }

    public Integer getAccountId() { return accountId; }
}