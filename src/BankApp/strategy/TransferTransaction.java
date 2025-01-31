package BankApp.strategy;

import BankApp.model.Transaction;
import BankApp.utils.LoggingService;

public class TransferTransaction implements TransactionStrategy {
    @Override
    public void execute(Transaction currentTransaction) {
        try {
            currentTransaction.getFrom().withdraw(currentTransaction.getAmount());
            currentTransaction.getTo().deposit(currentTransaction.getAmount());
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
    }
}
