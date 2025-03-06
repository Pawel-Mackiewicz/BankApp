package info.mackiewicz.bankapp.account.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.model.Account;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Component responsible for managing account locks during operations to prevent race conditions.
 * This manager ensures that operations affecting multiple accounts are performed atomically
 * by acquiring locks in a consistent order to avoid deadlocks.
 */
@Component
@Slf4j
public class AccountLockManager {
    @Getter
    private final AtomicInteger accountLockCounter = new AtomicInteger(0);
    @Getter
    private final AtomicInteger accountUnlockCounter = new AtomicInteger(0);

    private final Map<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();
    private final Map<Integer, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    // Helper method for acquiring/making locks for accounts
    private ReentrantLock getOrCreateLock(Integer accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    // Helper method for acquiring/making locks for users
    private ReentrantLock getOrCreateUserLock(Integer userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    /**
     * Acquires a lock for user operations like account creation.
     * This ensures atomic operations on user-specific data like nextAccountNumber.
     *
     * @param userId ID of the user to lock
     */
    public void lockUser(Integer userId) {
        log.debug("Acquiring lock for user ID: {}", userId);
        getOrCreateUserLock(userId).lock();
    }

    /**
     * Releases the lock for user operations.
     *
     * @param userId ID of the user to unlock
     */
    public void unlockUser(Integer userId) {
        log.debug("Releasing lock for user ID: {}", userId);
        getOrCreateUserLock(userId).unlock();
    }

    /**
     * Acquires locks for the specified accounts in a deadlock-free manner.
     * If both accounts are provided, locks are acquired in order of account IDs.
     * If only one account is provided, only that account is locked.
     *
     * @param from Source account to lock, may be null if not applicable
     * @param to Destination account to lock, may be null if not applicable
     */
    public void lockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            lockTwoAccounts(from, to);
        } else if (from != null) {
            lockAccount(from);
        } else if (to != null) {
            lockAccount(to);
        }
    }
    
    /**
     * Releases locks for the specified accounts in reverse order of acquisition.
     * If both accounts are provided, unlocks are performed in reverse order of account IDs.
     * If only one account is provided, only that account is unlocked.
     *
     * @param from Source account to unlock, may be null if not applicable
     * @param to Destination account to unlock, may be null if not applicable
     */
    public void unlockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            unlockTwoAccounts(from, to);
        } else if (from != null) {
            unlockAccount(from);
        } else if (to != null) {
            unlockAccount(to);
        }
    }

    private void lockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            lockAccount(acc1);
            lockAccount(acc2);
        } else {
            lockAccount(acc2);
            lockAccount(acc1);
        }
    }

    private void lockAccount(Account account) {
        log.debug("Acquiring lock for account ID: {}", account.getId());
        getOrCreateLock(account.getId()).lock();
        accountLockCounter.incrementAndGet();
    }

    private void unlockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            unlockAccount(acc2);
            unlockAccount(acc1);
        } else {
            unlockAccount(acc1);
            unlockAccount(acc2);
        }
    }

    private void unlockAccount(Account account) {
        log.debug("Releasing lock for account ID: {}", account.getId());
        getOrCreateLock(account.getId()).unlock();
        accountUnlockCounter.incrementAndGet();
    }
}