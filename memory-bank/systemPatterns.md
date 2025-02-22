# System Patterns & Architecture

## Dashboard UI Patterns [Updated: 2025-02-22 18:20]

### 1. IBAN Display

-   IBANs are displayed in the "My Accounts" section of the dashboard.
-   IBANs are now highlighted on hover using CSS `text-shadow`.
-   IBANs are copyable by clicking on them.

### 2. "Make a Transfer" Title

-   The "Make a Transfer" title is centered using inline CSS.

## Transaction System Patterns [Updated: 2025-02-22 17:22]

### 1. Struktura Transakcji
```java
public enum TransactionCategory {
    TRANSFER("Transfer"),   // przelewy
    DEPOSIT("Deposit"),     // wpłaty
    WITHDRAWAL("Withdrawal"), // wypłaty
    FEE("Fee")             // opłaty
}

public enum TransactionType {
    TRANSFER_OWN(
        TransactionCategory.TRANSFER,
        "Własne konto",
        true,   // wymaga IBAN
        0.0     // bez prowizji
    ),
    
    TRANSFER_INTERNAL(
        TransactionCategory.TRANSFER,
        "Przelew w banku",
        true,   // wymaga IBAN
        0.0     // bez prowizji
    ),
    
    TRANSFER_EXTERNAL(
        TransactionCategory.TRANSFER,
        "Przelew zewnętrzny",
        true,   // wymaga IBAN
        0.01    // prowizja 1%
    ),
    
    // Inne operacje
    DEPOSIT(...),
    WITHDRAWAL(...)
}
```

### 2. Strategie Transakcji
```java
// Interface dla wszystkich strategii
public interface TransactionStrategy {
    void process(Transaction transaction);
    void validate(Transaction transaction);
}

// Przykład implementacji dla przelewu
public class TransferTransaction implements TransactionStrategy {
    @Override
    public void process(Transaction transaction) {
        // Logika przelewu zależna od typu
        switch(transaction.getType()) {
            case TRANSFER_OWN: // logika przelewu własnego
            case TRANSFER_INTERNAL: // logika przelewu wewnętrznego
            case TRANSFER_EXTERNAL: // logika przelewu zewnętrznego
        }
    }
}
```

### 3. Filtrowanie Transakcji
Istniejący system filtrowania obsługuje:
- Filtrowanie po kategorii (np. "TRANSFER")
- Filtrowanie po konkretnym typie
- Filtrowanie po dacie
- Filtrowanie po kwocie
- Wyszukiwanie po tekście

```java
// Przykład użycia:
List<Transaction> filtered = filterService.filterTransactions(
    transactions,
    dateFrom,    // od kiedy
    dateTo,      // do kiedy
    "TRANSFER",  // kategoria lub typ
    minAmount,   // minimalna kwota
    maxAmount,   // maksymalna kwota
    searchQuery  // tekst do wyszukania
);
```

### 4. Walidacja
- Format IBAN w osobnej klasie IbanValidator
- Reguły biznesowe w TransactionService
- Walidacja na poziomie strategii transakcji

### 5. Wzorce
- Strategy Pattern dla różnych typów transakcji
- Builder Pattern dla tworzenia transakcji
- Factory Method dla strategii transakcji
- Validator Pattern dla walidacji