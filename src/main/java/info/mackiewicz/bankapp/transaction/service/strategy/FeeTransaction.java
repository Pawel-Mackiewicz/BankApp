package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeeTransaction implements TransactionStrategy {

    private static final Integer BANK_ACCOUNT_ID = -1;
    
    private final StrategyHelper strategyHelper;
    private final AccountService accountService;

    @Override
    public boolean execute(Transaction transaction) {
        try {
            // Set up bank account as destination for fee
            transaction.setDestinationAccount(accountService.getAccountById(BANK_ACCOUNT_ID));
            
            // Execute as transfer
            strategyHelper.transfer(transaction);
            return true;
        } catch (Exception e) {
            LoggingService.logErrorInMakingTransaction(transaction, e.getMessage());
            return false;
        }
    }
}
