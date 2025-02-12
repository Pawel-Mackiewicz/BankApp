package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.mackiewicz.bankapp.service.strategy.TransactionStrategy;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(length = 100)
    private String transactionTitle;

        @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @PrePersist
    public void prePersist() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    public boolean execute() {
        return strategy.execute(this);
    }

    @JsonIgnore
    public boolean isTransactionPossible() {
        if (this.type == TransactionType.DEPOSIT) {
            return this.destinationAccount != null;
        } else {
            return this.sourceAccount != null && this.sourceAccount.canWithdraw(this.amount);
        }
    }
}
