package BankApp.utils;

import BankApp.controller.AccountLockManager;
import BankApp.model.Account;

public class Util {

    public static void printIsTransactionsWorking(double sumOfAllAccountsBefore, double sumOfAllAccountsAfter) {
        double shouldBeZero = sumOfAllAccountsBefore - (sumOfAllAccountsAfter + Account.getDifferenceInAccounts());
        final double epsilon = 1e-9;
        boolean isLockingWorking = AccountLockManager.getAccountLockCounter().get() == AccountLockManager.getAccountUnlockCounter().get();

        System.out.println("Should be zero: " + shouldBeZero);
        if (Math.abs(shouldBeZero) < epsilon && isLockingWorking) {
            System.out.println("Your system is working :- )");
        } else {
            System.out.println("Your system is not working : <");
        }
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
    }
}
