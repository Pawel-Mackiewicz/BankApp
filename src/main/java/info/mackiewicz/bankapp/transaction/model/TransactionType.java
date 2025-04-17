package info.mackiewicz.bankapp.transaction.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionType {
    DEPOSIT(
        TransactionTypeCategory.DEPOSIT,
        "Deposit",
        false,   // no IBAN required
        0.0      // no fee
    ),
    WITHDRAWAL(
        TransactionTypeCategory.WITHDRAWAL,
        "Withdrawal",
        false,   // no IBAN required
        0.0      // no fee
    ),
    TRANSFER_OWN(
        TransactionTypeCategory.TRANSFER,
        "Own Account Transfer",
        true,    // requiresIban
        0.0      // fee
    ),
    TRANSFER_INTERNAL(
        TransactionTypeCategory.TRANSFER,
        "Internal Transfer",
        true,    // requiresIban
        0.0      // fee
    ),
    TRANSFER_EXTERNAL(
        TransactionTypeCategory.TRANSFER,
        "External Transfer",
        true,    // requiresIban
        0.01     // fee
    ),
    FEE(
        TransactionTypeCategory.FEE,
        "Fee",
        false,   // no IBAN required
        0.0      // no fee
    );

    @Getter
    private final TransactionTypeCategory category;
    
    @Getter
    private final String displayName;
    
    @Getter
    private final boolean requiresIban;
    
    @Getter
    private final double feePercentage;

    TransactionType(TransactionTypeCategory category, String displayName, boolean requiresIban, double feePercentage) {
        this.category = category;
        this.displayName = displayName;
        this.requiresIban = requiresIban;
        this.feePercentage = feePercentage;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name();
    }

    public static TransactionType fromString(String text) {
        for (TransactionType type : TransactionType.values()) {
            if (type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for " + text);
    }

    public boolean isFeeRequired() {
        return this.feePercentage > 0;
    }

    public String toString() {
        return displayName;
    }
}