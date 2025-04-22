package info.mackiewicz.bankapp.account.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class AccountLockException extends AccountBaseException {
    private final Integer accountId;
    private final int attempts;
    private final long totalWaitTime;
    private final boolean wasInterrupted;

    public AccountLockException(String message, Integer accountId, int attempts, long totalWaitTime, boolean wasInterrupted) {
        super(message, ErrorCode.INTERNAL_ERROR);
        this.accountId = accountId;
        this.attempts = attempts;
        this.totalWaitTime = totalWaitTime;
        this.wasInterrupted = wasInterrupted;
    }

    public Integer getAccountId() { return accountId; }
    public int getAttempts() { return attempts; }
    public long getTotalWaitTime() { return totalWaitTime; }
    public boolean wasInterrupted() { return wasInterrupted; }
}