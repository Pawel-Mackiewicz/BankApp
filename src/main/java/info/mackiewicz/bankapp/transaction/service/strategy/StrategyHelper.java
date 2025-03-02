package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;

import org.springframework.stereotype.Component;

@Component
public class StrategyHelper {
    private final AccountService accountService;

    public StrategyHelper(AccountService accountService) {
        this.accountService = accountService;
    }

    public void withdraw(Transaction transaction) {
        accountService.withdraw(transaction.getSourceAccount().getId(), transaction.getAmount());
    }

    public void deposit(Transaction transaction) {
        accountService.deposit(transaction.getDestinationAccount().getId(), transaction.getAmount());
    }
}
