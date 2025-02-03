package BankApp;

import BankApp.controller.AccountLockManager;
import BankApp.model.Account;
import BankApp.model.AccountService;
import BankApp.model.TransactionService;
import BankApp.model.TransactionType;
import BankApp.strategy.DepositTransaction;
import BankApp.strategy.FeeTransaction;
import BankApp.strategy.TransferTransaction;
import BankApp.strategy.WithdrawalTransaction;
import BankApp.utils.Util;


public class BankApp {
    public static void main(String[] args) {
        AccountService accountService = new AccountService();
        TransactionService transactionService = new TransactionService();

        accountService.initializeAccountsRandomly(100);

        double sumOfAllAccountsBefore = accountService.getSumOfAllAccounts();

        transactionService.generateRandomTransactions(TransactionType.TRANSFER, new TransferTransaction(), accountService, 50);
        transactionService.generateRandomTransactions(TransactionType.DEPOSIT, new DepositTransaction(), accountService, 50);
        transactionService.generateRandomTransactions(TransactionType.WITHDRAWAL, new WithdrawalTransaction(), accountService, 50);
        transactionService.generateRandomTransactions(TransactionType.FEE, new FeeTransaction(), accountService, 50);
        transactionService.waitForAllTransactions();

        double sumOfAllAccountsAfter = accountService.getSumOfAllAccounts();

        System.out.printf("BEFORE: %.2f\n", sumOfAllAccountsBefore);
        System.out.printf("AFTER: %.2f\n", sumOfAllAccountsAfter);
        System.out.printf("DIFFERENCE: %.2f\n", Account.getDifferenceInAccounts());
        System.out.println();
        System.out.printf("Lock counter: %s\n", AccountLockManager.getAccountLockCounter());
        System.out.printf("Unlock counter: %s\n", AccountLockManager.getAccountUnlockCounter());
        System.out.println();
        Util.printIsTransactionsWorking(sumOfAllAccountsBefore, sumOfAllAccountsAfter);
    }
}
