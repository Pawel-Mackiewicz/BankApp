package info.mackiewicz.bankapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionCategory {
    TRANSFER("Transfer"),
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    FEE("Fee");

    @Getter
    private final String displayName;

    TransactionCategory(String name) {
        this.displayName = name;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name();
    }

    public static TransactionCategory fromString(String text) {
        for (TransactionCategory c : TransactionCategory.values()) {
            if (c.displayName.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("No enum constant for " + text);
    }
}