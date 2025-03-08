package info.mackiewicz.bankapp.transaction.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.builder.DepositBuilder;
import info.mackiewicz.bankapp.transaction.model.builder.TransferBuilder;
import info.mackiewicz.bankapp.transaction.model.builder.WithdrawalBuilder;
import jakarta.persistence.CascadeType;
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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a financial transaction between accounts.
 * Pure entity class without any business logic.
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "source_id")
    private Account sourceAccount;
    
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "destination_id")
    private Account destinationAccount;
    
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
    void prePersist() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

        // Static fabric methods for creating builders
        public static TransferBuilder buildTransfer() {
            return new TransferBuilder();
        }
        
        public static WithdrawalBuilder buildWithdrawal() {
            return new WithdrawalBuilder();
        }
        
        public static DepositBuilder buildDeposit() {
            return new DepositBuilder();
        }
}
