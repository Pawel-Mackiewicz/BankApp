package BankApp;

import BankApp.model.AccountService;
import BankApp.model.TransactionService;
import BankApp.model.TransactionType;
import BankApp.utils.Util;

import java.util.logging.Logger;


public class BankApp {
    public static void main(String[] args) {
        AccountService accountService = new AccountService();
        TransactionService transactionService = new TransactionService();

        accountService.initializeAccountsRandomly(1000);

        int sumOfAllAccountsBefore = accountService.getSumOfAllAccounts();


        transactionService.generateRandomTransaction(TransactionType.TRANSFER, accountService, 100);
        transactionService.generateRandomTransaction(TransactionType.DEPOSIT, accountService, 100);
        transactionService.generateRandomTransaction(TransactionType.WITHDRAWAL, accountService, 100);
        transactionService.generateRandomTransaction(TransactionType.FEE, accountService, 100);
        transactionService.waitForAllTransactions();

        int sumOfAllAccountsAfter = accountService.getSumOfAllAccounts();

        System.out.println();
        System.out.println("Sum of all accounts before transactions: " + sumOfAllAccountsBefore);
        System.out.println("Sum of all accounts after transactions: " + sumOfAllAccountsAfter);
      //  Util.printIsTransactionsWorking(sumOfAllAccountsBefore, sumOfAllAccountsAfter);
    }
}
