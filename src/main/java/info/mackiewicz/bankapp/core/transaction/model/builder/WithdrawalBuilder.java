package info.mackiewicz.bankapp.core.transaction.model.builder;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;

public class WithdrawalBuilder extends AbstractTransactionBuilder<WithdrawalBuilder> {
    private Account sourceAccount;
    
    public WithdrawalBuilder() {
        this.type = TransactionType.WITHDRAWAL;
    }
    
    public WithdrawalBuilder from(Account account) {
        this.sourceAccount = account;
        return this;
    }
    
    @Override
    protected void validate() {
        validateAmount();
        if (sourceAccount == null) {
            throw new TransactionSourceAccountNotSpecifiedException();
        }
    }
    
    @Override
    public Transaction build() {
        validate();
        Transaction transaction = createBaseTransaction();
        transaction.setSourceAccount(sourceAccount);
        return transaction;
    }
}