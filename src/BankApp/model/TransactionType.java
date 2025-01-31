package BankApp.model;

public enum TransactionType {
    TRANSFER("Transfer"),
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    FEE("Fee");

    private final String displayName;

    TransactionType(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TransactionType fromString(String text) {
        for (TransactionType s : TransactionType.values()) {
            if (s.displayName.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant for " + text);
    }

}