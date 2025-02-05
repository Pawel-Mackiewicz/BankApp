package info.mackiewicz.bankapp.model;

import info.mackiewicz.bankapp.strategy.TransactionStrategy;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    private TransactionStatus status;

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
    @Getter
    private TransactionType type;

    public Transaction(Account account, BigDecimal amount, TransactionType type) {
        this(getFromAccount(account, type), getToAccount(account, type), amount, type);
    }

    public Transaction(Account from, Account to, BigDecimal amount, TransactionType type) {
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.NEW;
    }

    private static Account getToAccount(Account account, TransactionType type) {
        return type == TransactionType.DEPOSIT ? account : null;
    }

    private static Account getFromAccount(Account account, TransactionType type) {
        return type == TransactionType.DEPOSIT ? null : account;
    }

    public boolean execute() {
        return strategy.execute(this);
    }

    public boolean isTransactionPossible() {
        if (type == TransactionType.DEPOSIT) {
            return toAccount != null;
        }
        return fromAccount != null && fromAccount.canWithdraw(amount);
    }

}
