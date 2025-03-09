package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.transaction.model.Transaction;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WithdrawalTransaction implements TransactionStrategy {

    private final StrategyHelper strategyHelper;

    @Override
    public void execute(Transaction transaction) {
            strategyHelper.withdraw(transaction);
    }
}
