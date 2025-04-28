package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.TransactionExecutor;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import org.springframework.stereotype.Service;

/**
 * Service for executing DEPOSIT transactions.
 * Adds funds to the destination account.
 */
@Service
public class DepositTransactionExecutor implements TransactionExecutor {
    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        accountService.deposit(transaction.getDestinationAccount(), transaction.getAmount());
    }
    
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.DEPOSIT;
    }
}
