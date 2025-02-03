package BankApp.strategy;

import BankApp.model.Account;
import BankApp.model.Transaction;
import BankApp.utils.LoggingService;

public class DepositTransaction implements BankApp.strategy.TransactionStrategy {
    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            currentTransaction.getTo().deposit(currentTransaction.getAmount());
            Account.differenceFromDeposit(currentTransaction.getAmount());
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}
