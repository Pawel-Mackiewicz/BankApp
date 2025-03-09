package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.transaction.model.Transaction;

public interface TransactionStrategy {
    /**
     * Executes the transaction according to its type and rules.
     *
     * @param transaction Transaction to be executed
     * @return true if the transaction was executed successfully, false otherwise
     */
    void execute(Transaction transaction);
}
