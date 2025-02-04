package info.mackiewicz.bankapp.model;

import info.mackiewicz.bankapp.strategy.TransactionStrategy;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private Account fromAccount;
    @ManyToOne
    @JoinColumn(name = "to_id")
    private Account toAccount;

    @Transient
    private TransactionStrategy strategy;

    @Getter
    private BigDecimal amount;
    private TransactionType type;

    public boolean execute() {
        return strategy.execute(this);
    }

    public Transaction(Account account, BigDecimal amount, TransactionType type, TransactionStrategy strategy) {
        this(getFromAccount(account, type), getToAccount(account, type), amount, type, strategy);
    }

    public Transaction(Account from, Account to, BigDecimal amount, TransactionType type, TransactionStrategy strategy) {
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.type = type;
        this.strategy = strategy;
    }

    private static Account getToAccount(Account account, TransactionType type) {
        return type == TransactionType.DEPOSIT ? account : null;
    }

    private static Account getFromAccount(Account account, TransactionType type) {
        return type == TransactionType.DEPOSIT ? null : account;
    }

    public boolean isTransactionPossible() {
        if (type == TransactionType.DEPOSIT) {
            return toAccount != null;
        }
        return fromAccount != null && fromAccount.canWithdraw(amount);
    }

}
