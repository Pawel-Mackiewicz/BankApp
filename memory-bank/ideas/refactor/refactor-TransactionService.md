# Plan refaktoryzacji TransactionService

## Analiza problemów

Po analizie pliku `TransactionService.java` oraz powiązanych klas zidentyfikowałem następujące problemy:

1. **Naruszenie zasady pojedynczej odpowiedzialności (SRP)** - klasa wykonuje zbyt wiele zadań: tworzenie, pobieranie, usuwanie i przetwarzanie transakcji.

2. **Duplikacja kodu** - powtarzająca się logika walidacji i przetwarzania transakcji w metodach `processTransactionById` i `processAllNewTransactions`.

3. **Złożona logika warunkowa** - rozbudowany switch-case w metodzie `processTransactionById`.

4. **Niespójna obsługa typów transakcji** - specjalna obsługa dla `TRANSFER_OWN` w metodzie `createTransaction`.

5. **Mieszanie poziomów abstrakcji** - metody zawierają zarówno logikę biznesową, jak i szczegóły techniczne (logowanie).

6. **Niespójna obsługa wyjątków** - w niektórych miejscach wyjątki są łapane i logowane, w innych propagowane dalej.

7. **Potencjalne problemy ze współbieżnością** - użycie `CopyOnWriteArrayList` w metodzie `getAllNewTransactions`.

## Plan refaktoryzacji

### 1. Podział na mniejsze klasy zgodnie z SRP

Podzielimy `TransactionService` na trzy mniejsze klasy:

- **TransactionQueryService** - odpowiedzialny za pobieranie transakcji
  - `getTransactionById`
  - `getAllTransactions`
  - `getAllNewTransactions`
  - `getTransactionsByAccountId`
  - `getRecentTransactions`

- **TransactionCommandService** - odpowiedzialny za tworzenie i usuwanie transakcji
  - `createTransaction`
  - `deleteTransactionById`

- **TransactionProcessingService** - odpowiedzialny za przetwarzanie transakcji
  - `processTransactionById`
  - `processAllNewTransactions`
  - Metody pomocnicze do obsługi różnych statusów transakcji

- **TransactionService** - fasada dla powyższych serwisów, delegująca wywołania do odpowiednich implementacji

### 2. Refaktoryzacja metody processTransactionById

W klasie TransactionProcessingService wydzielimy metody pomocnicze dla każdego statusu transakcji:

- `processNewTransaction(Transaction)`
- `handleDoneTransaction(Transaction)`
- `handleFaultyTransaction(Transaction)`
- `handlePendingTransaction(Transaction)`

### 3. Poprawa obsługi wyjątków

- Ujednolicimy podejście do logowania i rzucania wyjątków
- Wprowadzimy bardziej opisowe komunikaty
- Zapewnimy spójne logowanie wyjątków

### 4. Poprawa obsługi współbieżności

- Przeanalizujemy użycie CopyOnWriteArrayList i ewentualnie zoptymalizujemy

### 5. Usunięcie duplikacji kodu

- Wydzielimy wspólną logikę z metod processTransactionById i processAllNewTransactions

### 6. Dodanie JavaDoc

- Dodamy dokumentację JavaDoc do wszystkich metod publicznych

## Struktura po refaktoryzacji

```
info.mackiewicz.bankapp.transaction.service/
├── TransactionService.java (fasada)
├── TransactionQueryService.java
├── TransactionCommandService.java
├── TransactionProcessingService.java
```

## Korzyści z refaktoryzacji

1. **Lepsza modularność** - każda klasa ma jedną, jasno określoną odpowiedzialność
2. **Łatwiejsze testowanie** - mniejsze klasy są łatwiejsze do przetestowania
3. **Większa elastyczność** - łatwiejsze dodawanie nowych funkcjonalności
4. **Lepsza czytelność kodu** - jaśniejsza struktura i nazewnictwo
5. **Zgodność z zasadami SOLID** - szczególnie SRP i DIP