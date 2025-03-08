package info.mackiewicz.bankapp.transaction.model;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.shared.exception.TransactionAmountNotSpecifiedException;
import info.mackiewicz.bankapp.shared.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.shared.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.shared.exception.TransactionTypeNotSpecifiedException;

@Component
@Scope("prototype")
public class TransactionBuilder {

    private Account sourceAccount;
    private Account destinationAccount;
    private TransactionType type;
    private final TransactionStatus status;
    private BigDecimal amount;
    private String title;

    public TransactionBuilder() {
        this.status = TransactionStatus.NEW;
    }

    public Transaction build() {
        validate();
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTitle(title);
        return transaction;
    }

    public TransactionBuilder withSourceAccount(Account account) {
        this.sourceAccount = account;
        return this;
    }

    public TransactionBuilder withDestinationAccount(Account account) {
        this.destinationAccount = account;
        return this;
    }

    public TransactionBuilder withType(String type) {
        this.type = TransactionType.valueOf(type.toUpperCase());
        return this;
    }

    public TransactionBuilder withType(TransactionType type) {
        this.type = type;
        return this;
    }

    public TransactionBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransactionBuilder withTransactionTitle(String title) {
        this.title = title;
        return this;
    }


    private void validate() {
        if (amount == null) {
            throw new TransactionAmountNotSpecifiedException();
        }
        if (type == null) {
            throw new TransactionTypeNotSpecifiedException();
        }
        // For transactions other than deposits, the source account must be specified
        if (sourceAccount == null && !TransactionType.DEPOSIT.equals(type)) {
            throw new TransactionSourceAccountNotSpecifiedException();
        }
        // For deposit transactions, the destination account must be specified
        if (destinationAccount == null && TransactionType.DEPOSIT.equals(type)) {
            throw new TransactionDestinationAccountNotSpecifiedException();
        }
    }
}
