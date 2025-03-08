package info.mackiewicz.bankapp.transaction.model.builder;

import java.math.BigDecimal;

import info.mackiewicz.bankapp.shared.exception.InvalidTransactionTypeException;
import info.mackiewicz.bankapp.shared.exception.TransactionAmountNotSpecifiedException;
import info.mackiewicz.bankapp.shared.exception.TransactionTypeNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

public abstract class AbstractTransactionBuilder<T extends AbstractTransactionBuilder<T>> {
    protected final TransactionStatus status = TransactionStatus.NEW;
    protected BigDecimal amount;
    protected String title;
    protected TransactionType type;

    public T withAmount(BigDecimal amount) {
        this.amount = amount;
        return self();
    }

    public T withTitle(String title) {
        this.title = title;
        return self();
    }
    
    protected void validateAmount() {
        if (amount == null) {
            throw new TransactionAmountNotSpecifiedException();
        }
    }

    protected void validateType() {
        if (type == null) {
            throw new TransactionTypeNotSpecifiedException();
        }
        if(!TransactionType.values().toString().contains(type.toString())) {
            throw new InvalidTransactionTypeException();
        }
    }

    protected Transaction createBaseTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setTitle(title);
        return transaction;
    }
    
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
    
    public abstract Transaction build();
    protected abstract void validate();
}