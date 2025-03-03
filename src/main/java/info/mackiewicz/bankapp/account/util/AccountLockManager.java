package info.mackiewicz.bankapp.account.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import info.mackiewicz.bankapp.account.model.Account;
import lombok.Getter;

/**
 * Utility class for managing account locks during operations to prevent race conditions.
 * This manager ensures that operations affecting multiple accounts are performed atomically
 * by acquiring locks in a consistent order to avoid deadlocks.
 */
public class AccountLockManager {
    @Getter
    public static AtomicInteger accountLockCounter = new AtomicInteger(0);
    @Getter
    public static AtomicInteger accountUnlockCounter = new AtomicInteger(0);

    private static final Map<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    // Helper method for acquiring/making locks for accounts
    private static ReentrantLock getOrCreateLock(Integer accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    /**
     * Acquires locks for the specified accounts in a deadlock-free manner.
     * If both accounts are provided, locks are acquired in order of account IDs.
     * If only one account is provided, only that account is locked.
     *
     * @param from Source account to lock, may be null if not applicable
     * @param to Destination account to lock, may be null if not applicable
     */
    public static void lockAccounts(Account from, Account to) {
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
    public static void unlockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            unlockTwoAccounts(from, to);
        } else if (from != null) {
            unlockAccount(from);
        } else if (to != null) {
            unlockAccount(to);
        }
    }

    private static void lockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            lockAccount(acc1);
            lockAccount(acc2);
        } else {
            lockAccount(acc2);
            lockAccount(acc1);
        }
    }

    private static void lockAccount(Account account) {
        getOrCreateLock(account.getId()).lock();
        accountLockCounter.incrementAndGet();
    }


    private static void unlockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            unlockAccount(acc2);
            unlockAccount(acc1);
        } else {
            unlockAccount(acc1);
            unlockAccount(acc2);
        }
    }

    private static void unlockAccount(Account account) {
        getOrCreateLock(account.getId()).unlock();
        accountUnlockCounter.incrementAndGet();
    }
}