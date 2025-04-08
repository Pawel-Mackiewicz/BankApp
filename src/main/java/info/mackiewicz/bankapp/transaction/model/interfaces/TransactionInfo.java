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

    /**
 * Retrieves the unique identifier of the transaction.
 *
 * @return the transaction's unique identifier
 */
Integer getId();
    /**
 * Retrieves the monetary amount involved in this transaction.
 *
 * @return a BigDecimal representing the transaction amount
 */
BigDecimal getAmount();
    /**
 * Returns the title or description of the transaction.
 *
 * @return the title or description of the transaction
 */
String getTitle();
    /**
 * Returns the current status of the transaction.
 *
 * @return a String representing the transaction's current status
 */
String getStatus();
    /**
 * Returns the transaction type.
 *
 * @return a String representing the type of transaction (e.g., "credit" or "debit")
 */
String getType();
    /**
 * Returns the date and time when the transaction occurred.
 *
 * @return a LocalDateTime instance representing the transaction's date and time
 */
LocalDateTime getDate();
}
