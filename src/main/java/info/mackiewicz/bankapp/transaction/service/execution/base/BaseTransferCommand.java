package info.mackiewicz.bankapp.transaction.service.execution.base;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutionCommand;

/**
 * Base implementation for transfer commands.
 * Implements standard transfer from source to destination account.
 */
public abstract class BaseTransferCommand implements TransactionExecutionCommand {
    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        accountService.withdraw(transaction.getSourceAccount(), transaction.getAmount());
        accountService.deposit(transaction.getDestinationAccount(), transaction.getAmount());
    }
}
