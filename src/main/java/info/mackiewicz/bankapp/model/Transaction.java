package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JoinColumn(name = "source_id")
    private Account sourceAccount;
    
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Account destinationAccount;
    
    @JsonIgnore
    @Transient
    private TransactionStrategy strategy;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    private BigDecimal amount;

    @JsonProperty("sourceAccountId")
    public Integer getSourceAccountId() {
        return sourceAccount != null ? sourceAccount.getId() : null;
    }

    @JsonProperty("destinationAccountId")
    public Integer getDestinationAccountId() {
        return destinationAccount != null ? destinationAccount.getId() : null;
    }

    public boolean execute() {
        return strategy.execute(this);
    }

    public boolean isTransactionPossible() {
        if (type == TransactionType.DEPOSIT) {
            return destinationAccount != null;
        }
        return sourceAccount != null && sourceAccount.canWithdraw(amount);
    }
}
