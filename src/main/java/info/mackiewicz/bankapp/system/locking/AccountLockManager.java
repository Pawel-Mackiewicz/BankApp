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
     * Acquires locks for one or two accounts in a deadlock-free manner.
     * <p>
     * When both accounts are provided (non-null), it locks them in order based on their IDs to prevent deadlocks.
     * If only one account is provided, the method locks that account.
     * </p>
     *
     * @param from the source account to lock, or null if not applicable
     * @param to   the destination account to lock, or null if not applicable
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
     * Acquires locks for two accounts in a consistent order to prevent deadlocks.
     *
     * <p>This method locks the account with the smaller ID first, ensuring that the order
     * of acquisition is predictable and deadlock-free.
     *
     * @param acc1 the first account
     * @param acc2 the second account
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
     * Releases locks for two accounts in reverse order of acquisition based on their IDs.
     * <p>
     * This method ensures that the account with the larger ID is unlocked first, maintaining
     * a consistent unlocking sequence that mirrors the locking order, which helps in preventing deadlocks.
     * </p>
     *
     * @param acc1 the first account involved in the unlock operation
     * @param acc2 the second account involved in the unlock operation
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
     * <p>Logs a debug message indicating the lock acquisition for the account using its unique ID,
     * then delegates the locking operation to the configured locking strategy.
     *
     * @param account the account to lock; must not be null
     */
    private void lock(Account account) {
        log.debug("Acquiring lock for account ID: {}", account.getId());
        lockingStrategy.lock(account.getId());
    }

    /**
     * Releases the lock associated with the specified account.
     *
     * This method logs the unlocking action and delegates to the locking strategy to release
     * the lock using the account's identifier.
     *
     * @param account the account whose lock is being released
     */
    private void unlock(Account account) {
        log.debug("Releasing lock for account ID: {}", account.getId());
        lockingStrategy.unlock(account.getId());
    }
}