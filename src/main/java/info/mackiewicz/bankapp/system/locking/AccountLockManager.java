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
     * Acquires locks on the provided accounts in a deadlock-free manner.
     * When both accounts are non-null, locks are acquired in order of their account IDs to prevent deadlocks.
     * When only one account is provided, only that account is locked.
     * If both accounts are null, no locking is performed.
     *
     * @param from the source account to lock; may be null if not applicable
     * @param to the destination account to lock; may be null if not applicable
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
     * Acquires locks for two accounts in a consistent order to avoid deadlocks.
     * <p>
     * Locks the account with the smaller ID first, followed by the account with the larger ID.
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
     * Releases locks on two accounts in reverse order of acquisition.
     *
     * <p>This method determines the unlocking order based on the accounts' IDs to ensure that locks are released
     * in the reverse order of their acquisition. The account with the higher ID (i.e. presumed to have been locked later)
     * is unlocked first, helping to prevent potential deadlocks.</p>
     *
     * @param acc1 the first account to unlock
     * @param acc2 the second account to unlock
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
     * <p>This method logs the lock acquisition attempt for the given account and delegates the 
     * actual locking operation to the locking strategy using the account’s ID.</p>
     *
     * @param account the account to lock
     */
    private void lock(Account account) {
        log.debug("Acquiring lock for account ID: {}", account.getId());
        lockingStrategy.lock(account.getId());
    }

    /**
     * Releases the lock for the specified account.
     * <p>
     * Logs the unlocking action and delegates the operation to the locking strategy based on the account's ID.
     * </p>
     *
     * @param account the account whose lock is being released
     */
    private void unlock(Account account) {
        log.debug("Releasing lock for account ID: {}", account.getId());
        lockingStrategy.unlock(account.getId());
    }
}