package info.mackiewicz.bankapp.system.notification.email.locking;

import info.mackiewicz.bankapp.core.account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Component responsible for managing account locks during operations to prevent
 * race conditions.
 * This manager ensures that operations affecting multiple accounts are
 * performed atomically
 * by acquiring locks in a consistent order to avoid deadlocks.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AccountLockManager {

    private final LockingStrategy lockingStrategy;

    /**
     * Acquires locks for the specified accounts in a deadlock-free manner.
     * If both accounts are provided, locks are acquired in order of account IDs.
     * If only one account is provided, only that account is locked.
     *
     * @param from Source account to lock, may be null if not applicable
     * @param to   Destination account to lock, may be null if not applicable
     */
    public void lockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            lockTwoAccounts(from, to);
        } else if (from != null) {
            lock(from);
        } else if (to != null) {
            lock(to);
        }
    }

    /**
     * Releases locks for the specified accounts in reverse order of acquisition.
     * If both accounts are provided, unlocks are performed in reverse order of
     * account IDs.
     * If only one account is provided, only that account is unlocked.
     *
     * @param from Source account to unlock, may be null if not applicable
     * @param to   Destination account to unlock, may be null if not applicable
     */
    public void unlockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            unlockTwoAccounts(from, to);
        } else if (from != null) {
            unlock(from);
        } else if (to != null) {
            unlock(to);
        }
    }

    private void lockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            lock(acc1);
            lock(acc2);
        } else {
            lock(acc2);
            lock(acc1);
        }
    }

    private void unlockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            unlock(acc2);
            unlock(acc1);
        } else {
            unlock(acc1);
            unlock(acc2);
        }
    }

    private void lock(Account account) {
        log.debug("Acquiring lock for account ID: {}", account.getId());
        lockingStrategy.lock(account.getId());
    }

    private void unlock(Account account) {
        log.debug("Releasing lock for account ID: {}", account.getId());
        lockingStrategy.unlock(account.getId());
    }
}