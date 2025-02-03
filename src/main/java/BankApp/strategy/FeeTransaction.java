package BankApp.strategy;

import BankApp.model.Account;
import BankApp.model.Transaction;
import BankApp.utils.LoggingService;

public class FeeTransaction implements BankApp.strategy.TransactionStrategy {
    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            currentTransaction.getFrom().withdraw(currentTransaction.getAmount());
            Account.BANK.deposit(currentTransaction.getAmount());
            Account.differenceFromWithdrawal(currentTransaction.getAmount());
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}
