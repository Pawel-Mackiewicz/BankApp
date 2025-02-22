package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.service.strategy.DepositTransaction;
import info.mackiewicz.bankapp.service.strategy.FeeTransaction;
import info.mackiewicz.bankapp.service.strategy.TransferTransaction;
import info.mackiewicz.bankapp.service.strategy.WithdrawalTransaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionHydrator {

    private static final Integer BANK_ACCOUNT_ID = -1;

    private final DepositTransaction depositTransaction;
    private final WithdrawalTransaction withdrawalTransaction;
    @SuppressWarnings("unused")
    private final FeeTransaction feeTransaction;
    private final TransferTransaction transferTransaction;
    private final AccountService accountService;

    public TransactionHydrator(
            DepositTransaction depositTransaction,
            WithdrawalTransaction withdrawalTransaction,
            FeeTransaction feeTransaction,
            TransferTransaction transferTransaction,
            AccountService accountService
    ) {
        this.depositTransaction = depositTransaction;
        this.withdrawalTransaction = withdrawalTransaction;
        this.feeTransaction = feeTransaction;
        this.transferTransaction = transferTransaction;
        this.accountService = accountService;
    }

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
