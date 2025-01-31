package BankApp.controller;

import BankApp.model.Account;
import BankApp.utils.LoggerSetup;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountLockManager {

    public static final Logger logger = LoggerSetup.getLogger(AccountLockManager.class.getName());

    public static void lockAccounts(Account from, Account to) {
        if (from != null && to != null) {
            lockTwoAccounts(from, to);
        } else if (from != null) {
            from.lock();
        } else if (to != null) {
            to.lock();
        }
    }

    private static void lockTwoAccounts(Account acc1, Account acc2) {
        if (acc1.getId() < acc2.getId()) {
            acc1.lock();
            acc2.lock();
        } else {
            acc2.lock();
            acc1.lock();
        }
    }

    public static void unlockAccounts(Account from, Account to) {
        if (to != null)     unlockAccount(to);
        if (from != null)   unlockAccount(from);
    }

    private static void unlockAccount(Account acc) {
            acc.unlock();
    }
}
