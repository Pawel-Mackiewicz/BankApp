package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.strategy.*;
import org.springframework.stereotype.Component;

@Component
public class TransactionHydrator {

    private final StrategyHelper strategyHelper;

    public TransactionHydrator(StrategyHelper strategyHelper) {
        this.strategyHelper = strategyHelper;
    }

    public Transaction hydrate(Transaction transaction) {

        switch (transaction.getType()) {
            case DEPOSIT -> transaction.setStrategy(new DepositTransaction(strategyHelper));
            case WITHDRAWAL -> transaction.setStrategy(new WithdrawalTransaction(strategyHelper));
            case FEE -> transaction.setStrategy(new FeeTransaction(strategyHelper));
            case TRANSFER -> transaction.setStrategy(new TransferTransaction(strategyHelper));
        }
        return transaction;
    }
}
