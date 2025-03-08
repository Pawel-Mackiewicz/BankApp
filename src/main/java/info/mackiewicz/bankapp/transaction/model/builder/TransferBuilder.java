package info.mackiewicz.bankapp.transaction.model.builder;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.shared.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.shared.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

public class TransferBuilder extends AbstractTransactionBuilder<TransferBuilder> {
    private Account sourceAccount;
    private Account destinationAccount;
    
    public TransferBuilder() {
    }

    public TransferBuilder withTransactionType(TransactionType type) {
        this.type = type;
        return this;
    }

    public TransferBuilder asOwnTransfer() {
        this.type = TransactionType.TRANSFER_OWN;
        return this;
    }

    public TransferBuilder asInternalTransfer() {
        this.type = TransactionType.TRANSFER_INTERNAL;
        return this;
    }



    public TransferBuilder asExternalTransfer() {
        this.type = TransactionType.TRANSFER_EXTERNAL;
        return this;
    }
    
    public TransferBuilder from(Account account) {
        this.sourceAccount = account;
        return this;
    }
    
    public TransferBuilder to(Account account) {
        this.destinationAccount = account;
        return this;
    }
    
    @Override
    protected void validate() {
        validateAmount();
        validateType();
        if (sourceAccount == null) {
            throw new TransactionSourceAccountNotSpecifiedException();
        }
        if (destinationAccount == null) {
            throw new TransactionDestinationAccountNotSpecifiedException();
        }
    }
    
    @Override
    public Transaction build() {
        validate();
        Transaction transaction = createBaseTransaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        return transaction;
    }
}