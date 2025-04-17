package info.mackiewicz.bankapp.transaction.model.adapter;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.interfaces.TransactionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Adapter class that wraps a Transaction object and exposes only the information
 * required by the TransactionInfo interface.
 */
@RequiredArgsConstructor
public class TransactionInfoAdapter implements TransactionInfo {
    
    private final Transaction transaction;
    
    @Override
    public Integer getId() {
        return transaction.getId();
    }
    
    @Override
    public BigDecimal getAmount() {
        return transaction.getAmount();
    }
    
    @Override
    public String getTitle() {
        return transaction.getTitle();
    }
    
    @Override
    public String getStatus() {
        return transaction.getStatus().getDisplayName();
    }
    
    @Override
    public String getType() {
        return transaction.getType().getDisplayName();
    }
    
    @Override
    public LocalDateTime getDate() {
        return transaction.getDate();
    }
    
    /**
     * Creates a TransactionInfo adapter from a Transaction object.
     * @return TransactionInfo instance wrapping the provided Transaction.
     * @throws NullPointerException if the transaction is null.
     * @see TransactionInfo
     * @see Transaction
     */
    public static TransactionInfo fromTransaction(@NonNull Transaction transaction) {
        return new TransactionInfoAdapter(transaction);
    }
}