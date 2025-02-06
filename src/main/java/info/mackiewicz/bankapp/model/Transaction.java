package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import info.mackiewicz.bankapp.service.strategy.TransactionStrategy;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "from_id")
    private Account fromAccount;
    @ManyToOne
    @JoinColumn(name = "to_id")
    private Account toAccount;
    @JsonIgnore
    @Transient
    private TransactionStrategy strategy;
    private TransactionStatus status;
    private TransactionType type;
    private BigDecimal amount;


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
