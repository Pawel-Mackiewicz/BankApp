# Decision Log

## 2025-03-03 - Account Class Interface Extraction

**Context:** 
The Account class currently has multiple responsibilities including financial operations, owner information management, and IBAN handling. This violates the Single Responsibility Principle and makes the class less maintainable.

**Decision:** 
Extract the following interfaces from the Account class:
1. `FinancialOperations` - For handling monetary transactions
2. `AccountOwnerInfo` - For managing owner-related information
3. `IbanHolder` - For IBAN-related operations

**Rationale:** 
- Improved separation of concerns
- Better maintainability and testability
- More flexible system evolution
- Easier to implement new account types in the future
- Better compliance with SOLID principles

**Implementation:** 
1. Create three new interfaces:
```java
public interface FinancialOperations {
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    boolean canWithdraw(BigDecimal amount);
    BigDecimal getBalance();
}

public interface AccountOwnerInfo {
    AccountOwnerDTO getOwnerDTO();
    Integer getOwnerId();
    User getOwner();
    void setOwner(User owner);
}

public interface IbanHolder {
    String getIban();
    String getFormattedIban();
}
```

2. Make Account class implement these interfaces:
```java
public class Account implements FinancialOperations, AccountOwnerInfo, IbanHolder {
    // existing implementation
}
```

**Architectural Impact:** 
- More modular and flexible account system
- Easier to implement different types of accounts in the future
- Better testability through interface-based design
- Clearer system boundaries and responsibilities
