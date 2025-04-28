package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.TransactionExecutor;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import org.springframework.stereotype.Service;

/**
 * Service for executing WITHDRAWAL transactions.
 * Withdraws funds from the source account.
 */
@Service
public class WithdrawalTransactionExecutor implements TransactionExecutor {
    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        accountService.withdraw(transaction.getSourceAccount(), transaction.getAmount());
    }
    
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAWAL;
    }
}
