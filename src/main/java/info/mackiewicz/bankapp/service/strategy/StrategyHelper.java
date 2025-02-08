package info.mackiewicz.bankapp.service.strategy;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.service.AccountService;
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
