package info.mackiewicz.bankapp.service.strategy;

import info.mackiewicz.bankapp.model.*;
import info.mackiewicz.bankapp.utils.LoggingService;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalTransaction implements TransactionStrategy {
    StrategyHelper strategyHelper;

    public WithdrawalTransaction(StrategyHelper strategyHelper) {
        this.strategyHelper = strategyHelper;
    }

    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            strategyHelper.withdraw(currentTransaction);
            //#FIXME Account.differenceFromWithdrawal(currentTransaction.getAmount());
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}
