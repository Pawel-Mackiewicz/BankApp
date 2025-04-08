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
 * Returns the unique identifier of the transaction.
 *
 * @return the transaction's unique ID
 */
Integer getId();
    /**
 * Returns the monetary amount involved in the transaction.
 *
 * <p>This value is represented as a BigDecimal to maintain precision for financial calculations.</p>
 *
 * @return the transaction amount
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
 * @return the status of the transaction as a string
 */
String getStatus();
    /**
 * Returns the type of the transaction.
 *
 * @return a string representing the transaction type
 */
String getType();
    /**
 * Returns the date and time when the transaction occurred.
 *
 * @return the transaction date and time
 */
LocalDateTime getDate();
}
