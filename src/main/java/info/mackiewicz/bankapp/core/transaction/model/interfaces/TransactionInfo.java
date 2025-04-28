package info.mackiewicz.bankapp.core.transaction.model.interfaces;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Interface representing the basic information of a transaction.
 * This interface is used to expose transaction data without revealing sensitive information.
 * @see Transaction
 * @see TransactionType
 * @see TransactionStatus
 */
public interface TransactionInfo {

    @Schema(description = "Unique transaction identifier", example = "20")
    Integer getId();

    @Schema(description = "Transaction amount", example = "100.00")
    BigDecimal getAmount();

    @Schema(description = "Transaction title", example = "For George")
    String getTitle();

    @Schema(description = "Transaction status")
    TransactionStatus getStatus();

    @Schema(description = "Transaction type", example = "TRANSFER_INTERNAL")
    TransactionType getType();

    @Schema(description = "Transaction date", example = "2025-05-05T12:00:00")
    LocalDateTime getDate();
}
