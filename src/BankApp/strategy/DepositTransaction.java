package BankApp.strategy;

import BankApp.model.Transaction;
import BankApp.utils.LoggingService;

public class DepositTransaction implements TransactionStrategy {
    @Override
    public void execute(Transaction currentTransaction) {
        try {
            currentTransaction.getTo().deposit(currentTransaction.getAmount());
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
    }
}
