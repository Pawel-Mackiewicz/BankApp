package info.mackiewicz.bankapp.transaction.service.execution.impl;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutionCommand;

/**
 * Service for executing WITHDRAWAL transactions.
 * Withdraws funds from the source account.
 */
@Service
public class WithdrawalTransactionCommand implements TransactionExecutionCommand {
    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        accountService.withdraw(transaction.getSourceAccount(), transaction.getAmount());
    }
    
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAWAL;
    }
}
