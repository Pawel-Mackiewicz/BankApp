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
 * @return the unique transaction ID
 */
Integer getId();
    /**
 * Returns the amount involved in the transaction.
 *
 * <p>This value is represented as a BigDecimal to ensure precision in financial calculations.</p>
 *
 * @return a BigDecimal representing the transaction amount
 */
BigDecimal getAmount();
    /**
 * Retrieves the title or description of the transaction.
 *
 * @return a string representing the transaction's title or description
 */
String getTitle();
    /**
 * Returns the current status of the transaction.
 *
 * @return a string representing the transaction status
 */
String getStatus();
    /**
 * Retrieves the type of the transaction.
 *
 * @return a String representing the transaction type
 */
String getType();
    /**
 * Returns the date and time when the transaction occurred.
 *
 * @return the transaction's date and time
 */
LocalDateTime getDate();
}
