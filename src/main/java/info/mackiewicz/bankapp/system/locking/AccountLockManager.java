package info.mackiewicz.bankapp.system.locking;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * Acquires locks for two accounts in ascending order of their IDs to prevent deadlocks.
     *
     * <p>This method compares the IDs of the two accounts and locks them in order, ensuring that the account
     * with the smaller ID is locked first. This consistent order of locking prevents deadlocks when multiple account locks
     * are acquired concurrently.
     *
     * @param acc1 the first account to lock
     * @param acc2 the second account to lock
     */
    private void lockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            lock(acc1);
            lock(acc2);
        } else {
            lock(acc2);
            lock(acc1);
        }
    }

    /**
     * Releases locks for two accounts in reverse order of acquisition.
     *
     * <p>The method determines the unlock order based on the account IDs to ensure that the account locked last is unlocked first,
     * thereby maintaining a consistent unlocking order that helps prevent deadlocks.</p>
     *
     * @param acc1 the first account involved in the unlocking operation
     * @param acc2 the second account involved in the unlocking operation
     */
    private void unlockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            unlock(acc2);
            unlock(acc1);
        } else {
            unlock(acc1);
            unlock(acc2);
        }
    }

    /**
     * Acquires a lock for the specified account.
     *
     * <p>This method logs a debug message with the account's ID and delegates the lock operation
     * to the configured locking strategy.</p>
     *
     * @param account the account to lock
     */
    private void lock(Account account) {
        log.debug("Acquiring lock for account ID: {}", account.getId());
        lockingStrategy.lock(account.getId());
    }

    /**
     * Releases the lock for the specified account.
     *
     * This method logs the unlocking operation and delegates to the configured locking strategy
     * using the account's unique identifier.
     *
     * @param account the account whose lock is to be released
     */
    private void unlock(Account account) {
        log.debug("Releasing lock for account ID: {}", account.getId());
        lockingStrategy.unlock(account.getId());
    }
}