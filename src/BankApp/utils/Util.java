package BankApp.utils;

public class Util {

    public static void printIsTransactionsWorking(int sumOfAllAccountsBefore, int sumOfAllAccountsAfter) {
        if (sumOfAllAccountsBefore == sumOfAllAccountsAfter) {
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
