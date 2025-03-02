package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;

import org.springframework.stereotype.Component;

@Component
public class TransferTransaction implements TransactionStrategy {

    private final StrategyHelper strategyHelper;

    public TransferTransaction(StrategyHelper strategyHelper) {
        this.strategyHelper = strategyHelper;
    }

    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            strategyHelper.withdraw(currentTransaction);
            strategyHelper.deposit(currentTransaction);
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}