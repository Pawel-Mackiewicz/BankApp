package info.mackiewicz.bankapp.service.strategy;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.utils.LoggingService;
import org.springframework.stereotype.Component;

@Component
public class FeeTransaction implements TransactionStrategy {

    private final StrategyHelper strategyHelper;

    public FeeTransaction(StrategyHelper strategyHelper) {
        this.strategyHelper = strategyHelper;
    }

    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            strategyHelper.withdraw(currentTransaction);
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}
