package info.mackiewicz.bankapp.transaction.model.builder;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.shared.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

public class DepositBuilder extends AbstractTransactionBuilder<DepositBuilder> {
    private Account destinationAccount;
    
    public DepositBuilder() {
        this.type = TransactionType.DEPOSIT;
    }
    
    public DepositBuilder to(Account account) {
        this.destinationAccount = account;
        return this;
    }
    
    @Override
    protected void validate() {
        validateAmount();
        if (destinationAccount == null) {
            throw new TransactionDestinationAccountNotSpecifiedException();
        }
    }
    
    @Override
    public Transaction build() {
        validate();
        Transaction transaction = createBaseTransaction();
        transaction.setDestinationAccount(destinationAccount);
        return transaction;
    }
}