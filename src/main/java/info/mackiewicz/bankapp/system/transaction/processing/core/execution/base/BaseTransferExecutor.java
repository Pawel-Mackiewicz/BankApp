package info.mackiewicz.bankapp.system.transaction.processing.core.execution.base;

import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.TransactionExecutor;

/**
 * Base implementation for transfer commands.
 * Implements standard transfer from source to destination account.
 */
public abstract class BaseTransferExecutor implements TransactionExecutor {
    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        accountService.withdraw(transaction.getSourceAccount(), transaction.getAmount());
        accountService.deposit(transaction.getDestinationAccount(), transaction.getAmount());
    }
}
