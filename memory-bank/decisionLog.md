# Decision Log

## [2025-02-22 18:20] - Dashboard UI Improvements

**Context:**
The user requested to display IBANs on the dashboard and asked for additional suggestions.

**Decision:**
1.  Highlight IBANs on hover to improve user experience.
2.  Center the "Make a Transfer" title for better visual balance.

**Rationale:**
1.  Highlighting IBANs makes them more noticeable and easier to copy.
2.  Centering the title improves the overall layout and aesthetics of the dashboard.

**Implementation:**
1.  Implemented text-shadow highlight on hover for IBANs using CSS.
2.  Centered the "Make a Transfer" title using inline CSS.

## [2025-02-22 17:22] - TransactionFilterService Review

**Context:**
Po dokładnej analizie istniejącego kodu TransactionFilterService, w szczególności metody filterByType, stwierdzono, że obecna implementacja jest w pełni wystarczająca.

**Analysis:**
1.  Obecna implementacja filterByType:
    ```java
    private boolean filterByType(Transaction transaction, String type) {
        if (type.equals("TRANSFER")) {
            return transaction.getType().getCategory().toString().equals("TRANSFER");
        }
        return transaction.getType().toString().equals(type);
    }
    ```
    - Już obsługuje filtrowanie po kategorii
    - Poprawnie filtruje po konkretnych typach
    - Jest wydajna i prosta w utrzymaniu

2.  Nie ma potrzeby wprowadzania zmian w:
    - Logice filtrowania
    - Strukturze kodu
    - Formacie parametrów

**Decision:**
Zachować obecną implementację TransactionFilterService bez zmian.

**Benefits:**
- Unikamy niepotrzebnych modyfikacji
- Zachowujemy sprawdzony, działający kod
- Eliminujemy ryzyko wprowadzenia błędów
- Oszczędzamy czas na rozwój innych funkcjonalności

## [Previous entries remain unchanged...]