package info.mackiewicz.bankapp.strategy;

import info.mackiewicz.bankapp.model.*;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.utils.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeeTransaction implements TransactionStrategy {
    StrategyHelper strategyHelper;

    public FeeTransaction(StrategyHelper strategyHelper) {
        this.strategyHelper = strategyHelper;
    }

    @Override
    public boolean execute(Transaction currentTransaction) {
        try {
            strategyHelper.withdraw(currentTransaction);
//            TODO: Account.BANK.deposit(currentTransaction.getAmount());
//            TODO: Account.differenceFromWithdrawal(currentTransaction.getAmount());
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(currentTransaction, e.getMessage());
        }
        return false;
    }
}
