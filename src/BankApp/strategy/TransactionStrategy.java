package BankApp.strategy;

import BankApp.model.Transaction;

public interface TransactionStrategy {
    void execute(Transaction currentTransaction);
}
