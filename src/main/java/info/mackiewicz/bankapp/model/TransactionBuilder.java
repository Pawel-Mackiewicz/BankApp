package info.mackiewicz.bankapp.model;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionBuilder {

    private Account fromAccount;
    private Account toAccount;
    private TransactionType type;
    private TransactionStatus status = TransactionStatus.NEW;
    private BigDecimal amount;

    public TransactionBuilder withFromAccount(Account account) {
        this.fromAccount = account;
        return this;
    }

    public TransactionBuilder withToAccount(Account account) {
        this.toAccount = account;
        return this;
    }

    public TransactionBuilder withType(String type) {
        this.type = TransactionType.valueOf(type.toUpperCase());
        return this;
    }

    public TransactionBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Transaction build() {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        return transaction;
    }

}
