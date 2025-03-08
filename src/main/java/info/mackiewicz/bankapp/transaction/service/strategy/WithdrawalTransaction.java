package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WithdrawalTransaction implements TransactionStrategy {

    private final StrategyHelper strategyHelper;

    @Override
    public boolean execute(Transaction transaction) {
        try {
            strategyHelper.withdraw(transaction);
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(transaction, e.getMessage());
            return false;
        }
    }
}
