package BankApp.strategy;

import BankApp.model.Transaction;

public interface TransactionStrategy {
    boolean execute(Transaction currentTransaction);
}
