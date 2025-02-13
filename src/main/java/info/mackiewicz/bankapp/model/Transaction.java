package info.mackiewicz.bankapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import info.mackiewicz.bankapp.service.strategy.TransactionStrategy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String title;

        @Column(name = "date")
    private LocalDateTime date;
    
    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDateTime.now();
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

    public String getOtherPartyName(Integer userId) {
        if (this.type == TransactionType.TRANSFER && this.sourceAccount.getOwnerId().equals(userId)) {
            return this.destinationAccount.getOwner().getUsername();
        } else if (this.type == TransactionType.TRANSFER && this.destinationAccount.getOwnerId().equals(userId)) {
            return this.sourceAccount.getOwner().getUsername();
        } else if (this.type == TransactionType.DEPOSIT) {
            return "Deposit";
        } else if (this.type == TransactionType.WITHDRAWAL) {
            return "Withdrawal";
        } else {
            return "Fees";
        }
    }
}
