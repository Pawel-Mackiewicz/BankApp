package BankApp.strategy;

import BankApp.model.Transaction;
import BankApp.utils.LoggingService;

public class TransferTransaction implements BankApp.strategy.TransactionStrategy {
    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            currentTransaction.getFrom().withdraw(currentTransaction.getAmount());
            currentTransaction.getTo().deposit(currentTransaction.getAmount());
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}
