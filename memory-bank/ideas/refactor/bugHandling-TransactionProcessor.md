# Plan refaktoryzacji obsługi błędów w TransactionProcessor

## Analiza obecnego kodu

Klasa `TransactionProcessor` jest odpowiedzialna za przetwarzanie transakcji finansowych z odpowiednimi mechanizmami blokowania i walidacji. Obecna implementacja obsługi błędów ma kilka problemów:

1. **Niespójne rzucanie wyjątków w metodzie asynchronicznej**:
   - Metoda `processTransaction` jest oznaczona jako `@Async`, co oznacza, że wyjątki rzucane w tej metodzie nie są propagowane do wywołującego.
   - Wszystkie metody obsługi błędów (`handleInsufficientFundsError`, `handleValidationError`, `handleUnexpectedError`) rzucają wyjątki, które mogą być utracone w kontekście asynchronicznym.

2. **Duplikacja kodu w metodach obsługi błędów**:
   - Każda metoda obsługi błędów wykonuje podobne operacje: logowanie, aktualizacja statusu, rzucanie wyjątku.

3. **Niespójność w obsłudze błędów**:
   - W metodzie `executeTransactionStrategy` rzucany jest `RuntimeException` z komunikatem "Transaction execution failed".
   - W metodzie `handleUnexpectedError` rzucany jest `RuntimeException` z komunikatem "Unexpected error during transaction processing".
   - Ta niespójność może prowadzić do problemów z testowalnością i debugowaniem.

4. **Brak mechanizmu powiadamiania o błędach**:
   - Nie ma mechanizmu powiadamiania użytkownika o błędach, co jest szczególnie ważne w kontekście metody asynchronicznej.

## Identyfikacja problemów

1. **Code Smells**:
   - **Duplikacja kodu**: Podobna logika w metodach obsługi błędów.
   - **Niespójne nazewnictwo**: Różne komunikaty błędów dla podobnych sytuacji.
   - **Rzucanie wyjątków w metodzie asynchronicznej**: Wyjątki mogą być utracone.
   - **Brak abstrakcji dla obsługi błędów**: Każdy typ błędu jest obsługiwany osobno.

2. **Potencjalne zagrożenia**:
   - **Utrata informacji o błędach**: Wyjątki rzucane w metodzie asynchronicznej mogą być utracone.
   - **Trudności w debugowaniu**: Niespójne komunikaty błędów utrudniają diagnozowanie problemów.
   - **Brak powiadomień o błędach**: Użytkownik może nie być świadomy, że transakcja się nie powiodła.

## Plan refaktoryzacji

### 1. Utworzenie dedykowanego wyjątku dla błędu wykonania transakcji

```java
package info.mackiewicz.bankapp.transaction.exception;

/**
 * Exception thrown when transaction execution fails
 */
public class TransactionExecutionException extends RuntimeException {
    
    public TransactionExecutionException(String message) {
        super(message);
    }

    public TransactionExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 2. Wprowadzenie wzorca obserwatora dla powiadamiania o błędach

```java
// Nowy interfejs dla obserwatorów błędów transakcji
package info.mackiewicz.bankapp.transaction.service.error;

import info.mackiewicz.bankapp.transaction.model.Transaction;

public interface TransactionErrorObserver {
    void onTransactionError(Transaction transaction, Exception error);
}
```

```java
// Nowa klasa do zarządzania obserwatorami
package info.mackiewicz.bankapp.transaction.service.error;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionErrorNotifier {
    private final List<TransactionErrorObserver> observers = new ArrayList<>();

    public void addObserver(TransactionErrorObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionErrorObserver observer) {
        observers.remove(observer);
    }

    public void notifyError(Transaction transaction, Exception error) {
        for (TransactionErrorObserver observer : observers) {
            observer.onTransactionError(transaction, error);
        }
    }
}
```

### 3. Wprowadzenie dedykowanej klasy do obsługi błędów

```java
// Nowa klasa do obsługi błędów transakcji
package info.mackiewicz.bankapp.transaction.service.error;

import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionErrorHandler {
    private final TransactionRepository repository;
    private final TransactionErrorNotifier errorNotifier;

    public void handleInsufficientFundsError(Transaction transaction, InsufficientFundsException e) {
        LoggingService.logFailedTransactionDueToInsufficientFunds(transaction);
        setTransactionStatus(transaction, TransactionStatus.INSUFFICIENT_FUNDS);
        errorNotifier.notifyError(transaction, e);
    }

    public void handleValidationError(Transaction transaction, TransactionValidationException e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Validation Error: " + e.getMessage());
        setTransactionStatus(transaction, TransactionStatus.VALIDATION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    public void handleExecutionError(Transaction transaction, TransactionExecutionException e) {
        LoggingService.logErrorInMakingTransaction(transaction);
        setTransactionStatus(transaction, TransactionStatus.EXECUTION_ERROR);
        errorNotifier.notifyError(transaction, e);
    }

    public void handleUnexpectedError(Transaction transaction, Exception e) {
        LoggingService.logErrorInMakingTransaction(transaction, "Unexpected Error: " + e.getMessage());
        setTransactionStatus(transaction, TransactionStatus.SYSTEM_ERROR);
        errorNotifier.notifyError(transaction, new RuntimeException("Unexpected error during transaction processing", e));
    }

    private void setTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
```

### 4. Refaktoryzacja klasy TransactionProcessor

```java
package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.shared.util.LoggingService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionExecutionException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.error.TransactionErrorHandler;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing financial transactions with proper locking and validation mechanisms.
 */
@RequiredArgsConstructor
@Service
public class TransactionProcessor {

    private final StrategyResolver strategyResolver;
    private final TransactionRepository repository;
    private final AccountLockManager accountLockManager;
    private final TransactionValidator validator;
    private final TransactionErrorHandler errorHandler;

    /**
     * Asynchronously processes a financial transaction with proper account locking and error handling.
     */
    @Async
    public void processTransaction(Transaction transaction) {
        LoggingService.logTransactionAttempt(transaction);
        acquireAccountLocks(transaction);
        try {
            executeTransactionProcess(transaction);
            LoggingService.logSuccessfulTransaction(transaction);
            setTransactionStatus(transaction, TransactionStatus.DONE);
        } catch (InsufficientFundsException e) {
            errorHandler.handleInsufficientFundsError(transaction, e);
        } catch (TransactionValidationException e) {
            errorHandler.handleValidationError(transaction, e);
        } catch (TransactionExecutionException e) {
            errorHandler.handleExecutionError(transaction, e);
        } catch (Exception e) {
            errorHandler.handleUnexpectedError(transaction, e);
        } finally {
            releaseAccountLocks(transaction);
        }
    }

    private void acquireAccountLocks(Transaction transaction) {
        accountLockManager.lockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logLockingAccounts(transaction);
    }

    private void executeTransactionProcess(Transaction transaction) {
        validateAndInitialize(transaction);
        executeTransactionStrategy(transaction);
    }

    private void validateAndInitialize(Transaction transaction) {
        validator.validate(transaction);
        setTransactionStatus(transaction, TransactionStatus.PENDING);
    }

    private void executeTransactionStrategy(Transaction transaction) {
        TransactionStrategy strategy = strategyResolver.resolveStrategy(transaction);
        boolean success = strategy.execute(transaction);
        
        if (!success) {
            throw new TransactionExecutionException("Transaction execution failed");
        }
    }

    private void releaseAccountLocks(Transaction transaction) {
        accountLockManager.unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        LoggingService.logUnlockingAccounts(transaction);
    }

    private void setTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        repository.save(transaction);
    }
}
```

### 5. Implementacja przykładowego obserwatora błędów

```java
// Przykładowy obserwator błędów
package info.mackiewicz.bankapp.transaction.service.error;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingErrorObserver implements TransactionErrorObserver {
    @Override
    public void onTransactionError(Transaction transaction, Exception error) {
        log.error("Transaction error: {} - {}", transaction.getId(), error.getMessage(), error);
    }
}
```

## Wdrożenie i weryfikacja

1. **Implementacja zmian**:
   - Utwórz nowe klasy: `TransactionExecutionException`, `TransactionErrorObserver`, `TransactionErrorNotifier`, `TransactionErrorHandler`, `LoggingErrorObserver`.
   - Zmodyfikuj klasę `TransactionProcessor` zgodnie z powyższymi propozycjami.

2. **Aktualizacja testów**:
   - Testy powinny być dostosowane do nowego podejścia do obsługi błędów.
   - Zamiast oczekiwać wyjątków, testy powinny weryfikować, czy status transakcji został odpowiednio zaktualizowany.

3. **Weryfikacja**:
   - Uruchom testy jednostkowe, aby upewnić się, że zmiany nie wprowadzają regresji.
   - Sprawdź, czy obsługa błędów działa zgodnie z oczekiwaniami w różnych scenariuszach.

## Korzyści z refaktoryzacji

1. **Eliminacja duplikacji kodu** - logika obsługi błędów jest scentralizowana w jednej klasie.
2. **Spójne podejście do obsługi błędów** - wszystkie błędy są obsługiwane w podobny sposób.
3. **Lepsza testowalność** - łatwiej jest testować obsługę błędów, gdy nie polegamy na rzucaniu wyjątków.
4. **Rozszerzalność** - łatwo można dodać nowe typy błędów i obserwatorów.
5. **Powiadamianie o błędach** - wprowadzenie mechanizmu powiadamiania pozwala na elastyczne reagowanie na błędy.