package info.mackiewicz.bankapp.core.transaction.model;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.builder.DepositBuilder;
import info.mackiewicz.bankapp.core.transaction.model.builder.TransferBuilder;
import info.mackiewicz.bankapp.core.transaction.model.builder.WithdrawalBuilder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction between accounts.
 * To create a new transaction, use one of the static builder methods.
 * @see #buildTransfer()
 * @see #buildWithdrawal()
 * @see #buildDeposit()
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
    @Column(length = 20)  // Longest enum value is INSUFFICIENT_FUNDS (18 chars) + margin
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
