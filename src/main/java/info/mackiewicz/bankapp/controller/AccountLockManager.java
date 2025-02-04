package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.model.Account;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class AccountLockManager {
    @Getter
    public static AtomicInteger accountLockCounter;
    @Getter
    public static AtomicInteger accountUnlockCounter;

    static {
        accountLockCounter = new AtomicInteger(0);
        accountUnlockCounter = new AtomicInteger(0);
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
    private static void lockAccount(Account account) {
        account.lock();
        accountLockCounter.incrementAndGet();
    }

    public static void unlockAccounts(Account from, Account to) {
        if (to != null)     unlockAccount(to);
        if (from != null)   unlockAccount(from);
    }

    private static void unlockAccount(Account acc) {
        acc.unlock();
        accountUnlockCounter.incrementAndGet();
    }
}