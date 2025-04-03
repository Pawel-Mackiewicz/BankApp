package info.mackiewicz.bankapp.transaction.model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.Transaction;

/**
 * Interface representing the basic information of a transaction.
 * This interface is used to expose transaction data without revealing sensitive information.
 * @see Transaction
 * @see TransactionType
 * @see TransactionStatus
 */
public interface TransactionInfo {

    Integer getId();
    BigDecimal getAmount();
    String getTitle();
    TransactionStatus getStatus();
    TransactionType getType();
    LocalDateTime getDate();
}
