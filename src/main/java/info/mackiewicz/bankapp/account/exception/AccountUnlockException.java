package info.mackiewicz.bankapp.account.exception;

public class AccountUnlockException extends RuntimeException {
    private final Integer accountId;

    public AccountUnlockException(String message, Integer accountId) {
        super(message);
        this.accountId = accountId;
    }

    public Integer getAccountId() { return accountId; }
}