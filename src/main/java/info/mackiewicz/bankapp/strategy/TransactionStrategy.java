package info.mackiewicz.bankapp.strategy;

import info.mackiewicz.bankapp.model.Transaction;

public interface TransactionStrategy {
    boolean execute(Transaction currentTransaction);
}
