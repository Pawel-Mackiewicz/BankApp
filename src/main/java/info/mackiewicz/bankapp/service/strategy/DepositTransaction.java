package info.mackiewicz.bankapp.service.strategy;

import info.mackiewicz.bankapp.model.*;
import info.mackiewicz.bankapp.utils.LoggingService;
import org.springframework.stereotype.Component;

@Component
public class DepositTransaction implements TransactionStrategy {

    private final StrategyHelper strategyHelper;

    public DepositTransaction(StrategyHelper strategyHelper) {
        this.strategyHelper = strategyHelper;
    }

    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            strategyHelper.deposit(currentTransaction);
          //  Account.differenceFromDeposit(currentTransaction.getAmount());
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}