# Plan refaktoryzacji systemu przetwarzania transakcji

## Analiza obecnego stanu

Obecny system przetwarzania transakcji składa się z kilku głównych komponentów:

1. **TransactionService** - fasada zapewniająca jednolite API dla klientów
2. **TransactionProcessingService** - zarządza cyklem życia transakcji i podejmuje decyzje o przetwarzaniu
3. **TransactionProcessor** - wykonuje faktyczne przetwarzanie transakcji z odpowiednim lockowaniem kont
4. **TransactionStatusManager** - zarządza zmianami statusów transakcji
5. **TransactionErrorHandler** - centralizuje obsługę błędów
6. **TransactionValidator** - waliduje poprawność transakcji

### Problemy w obecnym rozwiązaniu

1. Niejasny podział odpowiedzialności między TransactionProcessingService i TransactionProcessor
2. Duplikacja logiki walidacyjnej w różnych warstwach
3. Nieoptymalne wykorzystanie TransactionErrorHandler - niektóre błędy są obsługiwane bezpośrednio w usługach
4. Niespójne zarządzanie statusami transakcji - logika rozproszona między różne klasy
5. Brak wyraźnego rozgraniczenia logiki biznesowej od technicznej

## Plan refaktoryzacji

### 1. Wprowadzenie TransactionStatusChecker

Nowy komponent TransactionStatusChecker wyodrębni logikę związaną ze sprawdzaniem statusów transakcji i podejmowaniem decyzji na tej podstawie.

#### Klasa TransactionStatusChecker

- Przejmie metody canBeProcessed, isInProgress, isCompleted, hasFailed z TransactionStatusManager
- Doda metodę validateForProcessing dla centralnej walidacji statusu
- Będzie używana przez TransactionProcessingService do podejmowania decyzji biznesowych

### 2. Redefinicja odpowiedzialności klas

#### TransactionProcessingService
- **Odpowiedzialność**: zarządzanie cyklem życia transakcji, podejmowanie decyzji biznesowych
- **Zmiany**:
  - Użycie TransactionStatusChecker zamiast bezpośrednich sprawdzeń statusu
  - Przeprojektowanie metody processSafely do używania errorHandler
  - Uproszczenie metody processBasedOnStatus

#### TransactionProcessor
- **Odpowiedzialność**: wykonanie technicznego procesu transakcji z lockowaniem kont
- **Zmiany**:
  - Usunięcie duplikacji walidacji (validateAndInitialize)
  - Skupienie się na procesie execute-with-locks
  - Konsekwentne używanie errorHandler dla wszystkich błędów

#### TransactionStatusManager
- **Odpowiedzialność**: zarządzanie statusami w bazie danych
- **Zmiany**:
  - Usunięcie metod decyzyjnych (przeniesienie do TransactionStatusChecker)
  - Skupienie się na operacjach bazodanowych

### 3. Szczegółowe kroki refaktoryzacji

1. **Krok 1: Stworzenie klasy TransactionStatusChecker**
   - Implementacja metod do sprawdzania statusów
   - Implementacja metody validateForProcessing

2. **Krok 2: Modyfikacja TransactionProcessingService**
   - Dodanie zależności do TransactionStatusChecker
   - Refaktoryzacja metody processSafely:
     ```java
     private void processSafely(Transaction transaction) {
         try {
             validator.validate(transaction);
             statusChecker.validateForProcessing(transaction);
             processor.processTransaction(transaction);
         } catch (Exception e) {
             errorHandler.handleUnexpectedError(transaction, e);
         }
     }
     ```
   - Usunięcie metod handleDoneTransaction, handleFaultyTransaction, handlePendingTransaction, handleInvalidStatus
   - Uproszczenie metody processBasedOnStatus

3. **Krok 3: Modyfikacja TransactionProcessor**
   - Usunięcie metody validateAndInitialize
   - Zmiana executeTransactionProcess:
     ```java
     private void executeTransactionProcess(Transaction transaction) {
         statusManager.setTransactionStatus(transaction, TransactionStatus.PENDING);
         executeTransactionStrategy(transaction);
     }
     ```
   - Konsekwentne używanie errorHandler dla wszystkich typów błędów

4. **Krok 4: Modyfikacja TransactionStatusManager**
   - Usunięcie metod canBeProcessed, isInProgress, isCompleted, hasFailed
   - Uproszczenie API do zarządzania statusami w bazie danych

5. **Krok 5: Testy jednostkowe**
   - Aktualizacja testów TransactionProcessingServiceTest
   - Aktualizacja testów TransactionProcessorTest
   - Nowe testy dla TransactionStatusChecker

### 4. Nowy przepływ przetwarzania transakcji

1. Klient wywołuje TransactionService.processTransactionById(id)
2. TransactionService deleguje do TransactionProcessingService.processTransactionById(id)
3. TransactionProcessingService pobiera transakcję i wywołuje processSafely(transaction)
4. W processSafely:
   - Walidacja transakcji przez TransactionValidator
   - Sprawdzenie statusu przez TransactionStatusChecker
   - Jeśli wszystko OK, wywołanie TransactionProcessor.processTransaction()
5. W TransactionProcessor:
   - Lockowanie kont przez AccountLockManager
   - Zmiana statusu na PENDING przez TransactionStatusManager
   - Wykonanie strategii transakcyjnej
   - Zmiana statusu na DONE
   - Odblokowanie kont
6. W przypadku błędów na dowolnym etapie - TransactionErrorHandler ustawia odpowiedni status

### 5. Korzyści z refaktoryzacji

1. **Zasada Single Responsibility** - każda klasa ma jasno określoną odpowiedzialność
2. **Redukcja duplikacji kodu** - logika sprawdzania statusów i obsługi błędów scentralizowana
3. **Uproszczenie kodu** - mniej warunków i rozgałęzień w głównych klasach
4. **Łatwiejsze testowanie** - czystsze granice między komponentami
5. **Lepsze zarządzanie błędami** - konsekwentne użycie TransactionErrorHandler

### 6. Potencjalne wyzwania 

1. Konieczność aktualizacji wielu zależności między komponentami
2. Ryzyko regresji w skomplikowanej logice obsługi błędów
3. Zapewnienie poprawnej sekwencji operacji przy zmianie struktury TransactionProcessor

## Harmonogram

1. Implementacja TransactionStatusChecker - 1 dzień
2. Refaktoryzacja TransactionProcessingService - 1 dzień
3. Refaktoryzacja TransactionProcessor - 1 dzień
4. Aktualizacja testów jednostkowych - 1 dzień
5. Testy integracyjne i poprawki - 1 dzień

Szacowany czas realizacji całości: 5 dni roboczych.

## Kryteria akceptacji

1. Wszystkie testy jednostkowe i integracyjne przechodzą
2. Nie ma duplikacji kodu między klasami
3. Każda klasa ma pojedynczą, jasno określoną odpowiedzialność
4. Obsługa błędów jest scentralizowana w TransactionErrorHandler
5. Nowy kod jest dobrze udokumentowany