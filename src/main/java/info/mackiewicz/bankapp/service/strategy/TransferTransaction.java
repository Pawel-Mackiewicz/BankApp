package info.mackiewicz.bankapp.service.strategy;

import info.mackiewicz.bankapp.model.*;
import info.mackiewicz.bankapp.utils.LoggingService;
import org.springframework.stereotype.Component;

@Component
public class TransferTransaction implements TransactionStrategy {
    StrategyHelper strategyHelper;

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