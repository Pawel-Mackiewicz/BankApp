package info.mackiewicz.bankapp.transaction.model.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.interfaces.TransactionInfo;
import lombok.RequiredArgsConstructor;

/**
 * Adapter class that wraps a Transaction object and exposes only the information
 * required by the TransactionInfo interface.
 */
@RequiredArgsConstructor
public class TransactionInfoAdapter implements TransactionInfo {
    
    private final Transaction transaction;
    
    /**
     * Returns the ID of the underlying transaction.
     *
     * @return the transaction identifier
     */
    @Override
    public Integer getId() {
        return transaction.getId();
    }
    
    /**
     * Retrieves the monetary amount of the transaction.
     *
     * @return the transaction amount as a BigDecimal.
     */
    @Override
    public BigDecimal getAmount() {
        return transaction.getAmount();
    }
    
    /**
     * Returns the title of the encapsulated transaction.
     *
     * @return the transaction title
     */
    @Override
    public String getTitle() {
        return transaction.getTitle();
    }
    
    /**
     * Retrieves the name of the transaction's status.
     *
     * @return the transaction status name
     */
    @Override
    public String getStatus() {
        return transaction.getStatus().getName();
    }
    
    /**
     * Returns the name of the transaction's type.
     *
     * @return the name of the type associated with the transaction
     */
    @Override
    public String getType() {
        return transaction.getType().getName();
    }
    
    /**
     * Returns the date and time when the transaction occurred.
     *
     * @return a LocalDateTime representing the date and time of the transaction.
     */
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