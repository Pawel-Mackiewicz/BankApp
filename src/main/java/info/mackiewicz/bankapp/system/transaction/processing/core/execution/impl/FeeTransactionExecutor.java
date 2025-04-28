package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.TransactionExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for executing FEE transactions.
 * Withdraws a fee from the source account and transfers it to the bank account.
 */
@Service
public class FeeTransactionExecutor implements TransactionExecutor {
    private static final Integer BANK_ACCOUNT_ID = -1;

    @Override
    public void execute(Transaction transaction, AccountService accountService) {
        Account sourceAccount = transaction.getSourceAccount();
        BigDecimal amount = transaction.getAmount();

        // First ensure we have the destination account
        Account destinationAccount = transaction.getDestinationAccount();
        if (destinationAccount == null) {
            // Get and set bank account first before any operations
            destinationAccount = accountService.getAccountById(BANK_ACCOUNT_ID);
            transaction.setDestinationAccount(destinationAccount);
        }

        // Now proceed with the transfer operations
        accountService.withdraw(sourceAccount, amount);
        accountService.deposit(destinationAccount, amount);
    }
    
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.FEE;
    }
}
