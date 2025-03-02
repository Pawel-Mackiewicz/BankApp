package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.transaction.model.Transaction;

public interface TransactionStrategy {
    boolean execute(Transaction currentTransaction);
}
