package info.mackiewicz.bankapp.service.strategy;

import info.mackiewicz.bankapp.model.Transaction;

public interface TransactionStrategy {
    boolean execute(Transaction currentTransaction);
}
