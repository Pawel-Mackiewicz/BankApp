package BankApp.strategy;

import BankApp.model.Account;
import BankApp.model.Transaction;
import BankApp.utils.LoggingService;

public class FeeTransaction implements TransactionStrategy {
    @Override
    public void execute(Transaction currentTransaction) {
        try {
            currentTransaction.getFrom().withdraw(currentTransaction.getAmount());
            Account.BANK.deposit(currentTransaction.getAmount());
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
    }
}
