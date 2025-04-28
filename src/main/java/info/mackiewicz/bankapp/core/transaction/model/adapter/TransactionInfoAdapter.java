package info.mackiewicz.bankapp.core.transaction.model.adapter;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.transaction.model.interfaces.TransactionInfo;
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

    /**
     * Creates a TransactionInfo adapter from a Transaction object.
     *
     * @return TransactionInfo instance wrapping the provided Transaction.
     * @throws NullPointerException if the transaction is null.
     * @see TransactionInfo
     * @see Transaction
     */
    public static TransactionInfo fromTransaction(@NonNull Transaction transaction) {
        return new TransactionInfoAdapter(transaction);
    }

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
    public TransactionStatus getStatus() {
        return transaction.getStatus();
    }

    @Override
    public TransactionType getType() {
        return transaction.getType();
    }

    @Override
    public LocalDateTime getDate() {
        return transaction.getDate();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof TransactionInfoAdapter that)) return false;

        return transaction.equals(that.transaction);
    }

    @Override
    public int hashCode() {
        return transaction.hashCode();
    }
}