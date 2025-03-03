package info.mackiewicz.bankapp.account.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import info.mackiewicz.bankapp.account.model.Account;
import lombok.Getter;

public class AccountLockManager {
    @Getter
    public static AtomicInteger accountLockCounter = new AtomicInteger(0);
    @Getter
    public static AtomicInteger accountUnlockCounter = new AtomicInteger(0);

    private static final Map<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    // Metoda pomocnicza do pobierania/tworzenia locka
    private static ReentrantLock getOrCreateLock(Integer accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    public static void lockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            lockTwoAccounts(from, to);
        } else if (from != null) {
            lockAccount(from);
        } else if (to != null) {
            lockAccount(to);
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

    public static void lockAccount(Account account) {
        getOrCreateLock(account.getId()).lock();
        accountLockCounter.incrementAndGet();
    }

    public static void unlockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            unlockTwoAccounts(from, to);
        } else if (from != null) {
            unlockAccount(from);
        } else if (to != null) {
            unlockAccount(to);
        }
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