package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionType {
    TRANSFER_OWN(
        TransactionCategory.TRANSFER,
        "Own Account Transfer",
        true,    // requiresIban
        0.0      // fee
    ),
    TRANSFER_INTERNAL(
        TransactionCategory.TRANSFER,
        "Internal Transfer",
        true,    // requiresIban
        0.0      // fee
    ),
    TRANSFER_EXTERNAL(
        TransactionCategory.TRANSFER,
        "External Transfer",
        true,    // requiresIban
        0.01     // fee
    ),
    DEPOSIT(
        TransactionCategory.DEPOSIT,
        "Deposit",
        false,   // no IBAN required
        0.0      // no fee
    ),
    WITHDRAWAL(
        TransactionCategory.WITHDRAWAL,
        "Withdrawal",
        false,   // no IBAN required
        0.0      // no fee
    ),
    FEE(
        TransactionCategory.FEE,
        "Fee",
        false,   // no IBAN required
        0.0      // no fee
    );

    @Getter
    private final TransactionCategory category;
    
    @Getter
    private final String displayName;
    
    @Getter
    private final boolean requiresIban;
    
    @Getter
    private final double feePercentage;

    TransactionType(TransactionCategory category, String displayName, boolean requiresIban, double feePercentage) {
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
}