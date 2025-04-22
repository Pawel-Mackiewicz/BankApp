package info.mackiewicz.bankapp.system.transaction.processing.core.execution;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

/**
 * Interface for transaction execution commands.
 * Implementations define how to execute specific transaction types.
 */
public interface TransactionExecutor {
    /**
     * Executes a financial transaction.
     *
     * @param transaction transaction to execute
     * @param accountService service for account operations
     */
    void execute(Transaction transaction, AccountService accountService);
    
    /**
     * Returns the transaction type supported by this command.
     *
     * @return transaction type
     */
    TransactionType getTransactionType();
}
