package info.mackiewicz.bankapp.shared.error;

/**
 * Represents different domains of error handling in the application.
 * Provides type-safe domain identification and validation.
 */
public enum ErrorDomain {
    COMMON("common"),
    SECURITY("security"),
    TRANSACTION("transaction");
    
    private final String value;
    
    ErrorDomain(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Find ErrorDomain by its string value.
     * @param value the string representation of domain
     * @return ErrorDomain if found, null otherwise
     */
    public static ErrorDomain fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (ErrorDomain domain : ErrorDomain.values()) {
            if (domain.value.equals(value)) {
                return domain;
            }
        }
        return null;
    }
}