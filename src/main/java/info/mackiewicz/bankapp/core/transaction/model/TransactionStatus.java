package info.mackiewicz.bankapp.core.transaction.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Represents the current status of a transaction with its category and display name.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionStatus {
    // Processing statuses
    NEW(
        TransactionStatusCategory.PROCESSING,
        "New"
    ),
    PENDING(
        TransactionStatusCategory.PROCESSING,
        "In Progress"
    ),

    // Success status
    DONE(
        TransactionStatusCategory.SUCCESS,
        "Completed"
    ),

    // Error statuses - all use FAULTY category
    INSUFFICIENT_FUNDS(
        TransactionStatusCategory.FAULTY,
        "Insufficient Funds"
    ),
    VALIDATION_ERROR(
        TransactionStatusCategory.FAULTY,
        "Validation Failed"
    ),
    EXECUTION_ERROR(
        TransactionStatusCategory.FAULTY,
        "Execution Failed"
    ),
    SYSTEM_ERROR(
        TransactionStatusCategory.FAULTY,
        "System Error"
    ),
    // Legacy status - for backward compatibility
    @Deprecated
    FAULTY(
        TransactionStatusCategory.FAULTY,
        "Failed"
    );

    @Getter
    private final TransactionStatusCategory category;

    @Getter
    private final String displayName;

    TransactionStatus(TransactionStatusCategory category, String displayName) {
        this.category = category;
        this.displayName = displayName;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name();
    }

    /**
     * Indicates if this status represents a failed state
     */
    public boolean isFailed() {
        return category == TransactionStatusCategory.FAULTY;
    }

    /**
     * Indicates if this status represents a final state (success or failure)
     */
    public boolean isFinal() {
        return this == DONE || isFailed();
    }

    /**
     * Indicates if this status represents a processing state
     */
    public boolean isProcessing() {
        return category == TransactionStatusCategory.PROCESSING;
    }
}
