package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.strategy.DepositTransaction;
import info.mackiewicz.bankapp.transaction.service.strategy.FeeTransaction;
import info.mackiewicz.bankapp.transaction.service.strategy.TransferTransaction;
import info.mackiewicz.bankapp.transaction.service.strategy.WithdrawalTransaction;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionHydrator {

    private static final Integer BANK_ACCOUNT_ID = -1;

    private final DepositTransaction depositTransaction;
    private final WithdrawalTransaction withdrawalTransaction;
    @SuppressWarnings("unused")
    private final FeeTransaction feeTransaction;
    private final TransferTransaction transferTransaction;
    private final AccountService accountService;


    public Transaction hydrate(Transaction transaction) {
        switch (transaction.getType().getCategory()) {
            case DEPOSIT -> transaction.setStrategy(depositTransaction);
            case WITHDRAWAL -> transaction.setStrategy(withdrawalTransaction);
            case TRANSFER -> transaction.setStrategy(transferTransaction);
            case FEE -> {
                Account bankAccount = accountService.getAccountById(BANK_ACCOUNT_ID);
                transaction.setDestinationAccount(bankAccount);
                transaction.setStrategy(transferTransaction);
            }
        }
        return transaction;
    }
}
