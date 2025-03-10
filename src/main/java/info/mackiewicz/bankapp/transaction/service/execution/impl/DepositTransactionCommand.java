package info.mackiewicz.bankapp.transaction.service.execution.impl;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.execution.TransactionExecutionCommand;

/**
 * Service for executing DEPOSIT transactions.
 * Adds funds to the destination account.
 */
@Service
public class DepositTransactionCommand implements TransactionExecutionCommand {
    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        accountService.deposit(transaction.getDestinationAccount(), transaction.getAmount());
    }
    
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.DEPOSIT;
    }
}
