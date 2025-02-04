package info.mackiewicz.bankapp.strategy;

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
        accountService.withdraw(transaction.getFromAccount().getId(), transaction.getAmount());
    }

    public void deposit(Transaction transaction) {
        accountService.deposit(transaction.getToAccount().getId(), transaction.getAmount());
    }
}
