package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;

public interface TransactionStrategy {
    /**
     * Executes the transaction according to its type and rules.
     *
     * @param transaction Transaction to be executed
     * @throws InsufficientFundsException when there are not enough funds in the source account
     * @throws TransactionValidationException when transaction validation fails
     * @throws TransactionExecutionException when there is an error during transaction execution
     * @throws IllegalArgumentException when transaction parameters are invalid (e.g., null type)
     * @throws RuntimeException for unexpected errors during processing
     */
    void execute(Transaction transaction);
}
