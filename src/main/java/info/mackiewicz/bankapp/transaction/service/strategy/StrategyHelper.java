package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
class StrategyHelper {
    private final AccountService accountService;

    public void withdraw(Transaction transaction) {
        accountService.withdraw(transaction.getSourceAccount(), transaction.getAmount());
    }

    public void deposit(Transaction transaction) {
        accountService.deposit(transaction.getDestinationAccount(), transaction.getAmount());
    }

    @Transactional
    public void transfer(Transaction transaction) {
        withdraw(transaction);
        deposit(transaction);
    }
}
