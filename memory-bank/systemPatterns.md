# System Patterns & Architecture

## Dashboard UI Patterns [Updated: 2025-02-22 20:11]

### 1. IBAN Display
- IBANs are displayed in the "My Accounts" section of the dashboard.
- IBANs are now highlighted on hover using CSS `text-shadow`.
- IBANs are copyable by clicking on them.

### 2. Transfer Interface
- Tabbed interface for different transfer types
- Dynamic form validation
- Real-time balance and IBAN display
- Responsive design patterns

### 3. Form Validation Patterns
```javascript
// Real-time validation pattern
async function validateInput(input) {
    const value = input.value;
    const type = input.dataset.type;
    
    try {
        const response = await fetch(`/api/validate-${type}?value=${value}`);
        const data = await response.json();
        
        if (data.valid) {
            setValid(input);
        } else {
            setInvalid(input, data.message);
        }
    } catch (error) {
        setInvalid(input, 'Validation error');
    }
}
```

## Transaction System Patterns [Updated: 2025-02-22 20:11]

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
- Nowe wzorce walidacji:
  * Walidacja email przez AccountRepository
  * Walidacja dostępnych środków
  * Walidacja uprawnień do kont

### 5. Wzorce
- Strategy Pattern dla różnych typów transakcji
- Builder Pattern dla tworzenia transakcji
- Factory Method dla strategii transakcji
- Validator Pattern dla walidacji
- Observer Pattern dla aktualizacji UI
- Command Pattern dla operacji transferowych

### 6. Nowe Wzorce UI
```javascript
// Tab Management Pattern
class TabManager {
    constructor(tabContainer, contentContainer) {
        this.tabs = tabContainer.querySelectorAll('[data-tab]');
        this.contents = contentContainer.querySelectorAll('[data-content]');
        
        this.tabs.forEach(tab => {
            tab.addEventListener('click', () => this.switchTab(tab));
        });
    }
    
    switchTab(activeTab) {
        // Deaktywuj wszystkie
        this.tabs.forEach(tab => tab.classList.remove('active'));
        this.contents.forEach(content => content.classList.remove('active'));
        
        // Aktywuj wybrany
        activeTab.classList.add('active');
        const content = document.querySelector(
            `[data-content="${activeTab.dataset.tab}"]`
        );
        content.classList.add('active');
    }
}

// Form State Management Pattern
class FormStateManager {
    constructor(form) {
        this.form = form;
        this.state = {};
        this.validators = {};
        
        this.setupValidators();
        this.setupListeners();
    }
    
    async validate(field) {
        const validator = this.validators[field.name];
        if (validator) {
            return await validator(field.value);
        }
        return true;
    }
}