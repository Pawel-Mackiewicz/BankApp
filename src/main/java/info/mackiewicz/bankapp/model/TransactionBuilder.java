package info.mackiewicz.bankapp.model;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.exception.TransactionAmountNotSpecifiedException;
import info.mackiewicz.bankapp.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.exception.TransactionTypeNotSpecifiedException;
import info.mackiewicz.bankapp.service.AccountService;

@Component
@Scope("prototype")
public class TransactionBuilder {

    private final AccountService accountService;

    private Account sourceAccount;
    private Account destinationAccount;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String title;

    public TransactionBuilder(AccountService accountService) {
        this.accountService = accountService;
        this.status = TransactionStatus.NEW;
    }

    public TransactionBuilder withSourceAccount(Integer accountId) {
        this.sourceAccount = accountId == null ? null : accountService.getAccountById(accountId);
        return this;
    }

    public TransactionBuilder withDestinationAccount(Integer accountId) {
        this.destinationAccount = accountId == null ? null : accountService.getAccountById(accountId);
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
