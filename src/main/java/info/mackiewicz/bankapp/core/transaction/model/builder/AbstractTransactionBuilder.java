package info.mackiewicz.bankapp.core.transaction.model.builder;

import info.mackiewicz.bankapp.core.transaction.exception.InvalidTransactionTypeException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionAmountNotSpecifiedException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionTypeNotSpecifiedException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.util.Arrays;

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
        if(!Arrays.asList(TransactionType.values()).contains(type)) {
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